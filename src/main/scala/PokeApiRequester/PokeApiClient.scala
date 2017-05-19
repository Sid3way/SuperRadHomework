package PokeApiRequester

import Common.{FetchPokemonByIdRequest, FetchPokemonByIdResponse}
import akka.actor.{Actor, ActorRef, Stash}
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
class PokeApiClient extends Actor with Stash{

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher

  val http = Http(context.system)

  var lastSender : ActorRef = ActorRef.noSender
  var lastRequest = FetchPokemonByIdRequest(0)

  def RequestApi(request : FetchPokemonByIdRequest): Unit = {
    lastSender = sender
    lastRequest = request
    context.become(waitingForResponse)
    val pokeApiUrl = "http://pokeapi.co/api/v2/pokemon/" + request.id + "/"
    var futureResult = http.singleRequest(HttpRequest(GET, uri = pokeApiUrl))
    futureResult pipeTo self

  }

  def onResultReceived(entity: ResponseEntity) : Unit = {
    entity.dataBytes.map(_.utf8String).runForeach(body => {
      println("Received result from pokeApi: " + body)
      val json : JsValue = Json.parse(body)
      val pokeResult : JsResult[PokemonModel] = json.validate[PokemonModel]
      pokeResult match {
        case success : JsSuccess[PokemonModel] => println("json parsed successfuly ! Contains: " + success.get)
        case fail : JsError => println("couldn't parse json retrieved from api. Error: " + JsError.toJson(fail).toString())
      }})
      lastSender.tell(FetchPokemonByIdResponse(lastRequest, "toBeReplaced"), self)
      unstashAll()
      context.unbecome()
    }

  def waitingForResponse : Receive = {
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      onResultReceived(entity)
    case Failure(_) =>
      println("Error while fetching entities in pokeApi :'(")
      unstashAll()
      context.unbecome()
    case _ => stash()
  }

  override def receive: Receive = {
    case request : FetchPokemonByIdRequest =>
      RequestApi(request)
  }
}

