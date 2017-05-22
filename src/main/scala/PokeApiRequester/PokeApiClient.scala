package PokeApiRequester

import Common.{FetchPokemonByIdRequest, FetchPokemonByIdResponse, RequestStatus}
import akka.actor.{Actor, ActorRef, PoisonPill, Props, Stash}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}

import scala.concurrent.ExecutionContextExecutor
import akka.pattern._
import HttpMethods._
import PokemonsDataStore.PokemonModel.PokemonModel
import play.api.libs.json._

import scala.util.Failure

/**
  * Created by Damie on 17/05/2017.
  */
class PokeApiClient extends Actor {

  override def receive: Receive = {
    case request : FetchPokemonByIdRequest =>
      println("Received request, creating a worker to handle it")
      context.actorOf(Props[PokeApiWorker]).forward(request)
  }
}

class PokeApiWorker extends Actor {
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher

  val http = Http(context.system)

  var lastSender : ActorRef = ActorRef.noSender
  var lastRequest = FetchPokemonByIdRequest(0)

  def RequestApi(request : FetchPokemonByIdRequest): Unit = {
    println("Preparing to send request to API")
    lastSender = sender
    lastRequest = request
    println("Changing context")
    context.become(waitingForResponse)
    val pokeApiUrl = "http://pokeapi.co/api/v2/pokemon/" + request.id + "/"
    println("Sending request")
    var futureResult = http.singleRequest(HttpRequest(GET, uri = pokeApiUrl))
    println("Piping result to self")
    futureResult pipeTo self
    println("Done")
  }

  def onResultReceived(entity: ResponseEntity) : Unit = {
    entity.dataBytes.map(_.utf8String).runForeach(body => {
      println("Received result from pokeApi: " + body)
      val json : JsValue = Json.parse(body)
      val pokeResult : JsResult[PokemonModel] = json.validate[PokemonModel]
      pokeResult match {
        case success : JsSuccess[PokemonModel] =>
          val pokemonModel = success.get
          println("json parsed successfuly ! Contains: " + pokemonModel)
          lastSender.tell(FetchPokemonByIdResponse(lastRequest, pokemonModel, RequestStatus.Success), self)
        case fail : JsError =>
          println("couldn't parse json retrieved from api. Error: " + JsError.toJson(fail).toString())
          lastSender.tell(FetchPokemonByIdResponse(lastRequest, null, RequestStatus.Error), self)
        case _ => println("dunno, lol")
      }})
  }

  def waitingForResponse : Receive = {
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      println("Received ok response")
      onResultReceived(entity)
      self ! PoisonPill
    case Failure(_) =>
      println("Error while fetching entities in pokeApi :'(")
      lastSender.tell(FetchPokemonByIdResponse(lastRequest, null, RequestStatus.Error), self)
      self ! PoisonPill
    case _ =>
      println("Dude what")
      lastSender.tell(FetchPokemonByIdResponse(lastRequest, null, RequestStatus.Error), self)
      self ! PoisonPill
  }

  override def receive: Receive = {
    case request : FetchPokemonByIdRequest =>
      RequestApi(request)
  }

}
