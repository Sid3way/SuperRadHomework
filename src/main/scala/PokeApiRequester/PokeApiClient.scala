package PokeApiRequester

import java.io.{FileReader, FileWriter}
import java.nio.file.{Files, Paths}


import Common.{FetchPokemonByIdRequest, FetchPokemonByIdResponse, RequestStatus}
import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}

import scala.concurrent.duration._
import akka.util.ByteString
import akka.http.scaladsl.model._

import scala.concurrent.ExecutionContextExecutor
import HttpMethods._
import PokemonsDataStore.PokemonModel.PokemonModel
import play.api.libs.json._

import scala.util.{Failure, Success}
import PokemonsDataStore.PokemonModel.PokemonModel.pokemonReads
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
    lastSender = sender
    lastRequest = request
    val file = "C:\\pokemonData\\" + request.id + ".data"
    val filePath = Paths.get(file)
    if (Files.exists(filePath)) {
      val body = new String(Files.readAllBytes(filePath))
      println("Retrieved from cache : " + body)
      ParseBody(body, true)
    }
    else {
      context.become(waitingForResponse)
      val pokeApiUrl = "http://pokeapi.co/api/v2/pokemon/" + request.id + "/"
      val futureResult = http.singleRequest(HttpRequest(GET, uri = pokeApiUrl)).flatMap(f => f.entity.toStrict(3.second))

      futureResult onComplete {
        case Success(result)  => self ! result
        case Failure(failure) => {
          println(s"ID=${lastRequest.id} Error !")
          self ! PoisonPill
          lastSender.tell(FetchPokemonByIdResponse(lastRequest, null, RequestStatus.Error), self)
        }
      }
    }
  }

  def ParseBody(body: String, isFromCache : Boolean): Unit = {
    val json: JsValue = Json.parse(body)
    val pokeResult: JsResult[PokemonModel] = json.validate[PokemonModel]
    pokeResult match {
      case success: JsSuccess[PokemonModel] =>
        val pokemonModel = success.get
        if (!isFromCache)
          {
            val filePath = "C:\\pokemonData\\" + pokemonModel.id + ".data"
            val fw = new FileWriter(filePath, true)
            fw.write(json.toString())
            fw.close()
          }
        lastSender.tell(FetchPokemonByIdResponse(lastRequest, pokemonModel, RequestStatus.Success), self)
        self ! PoisonPill
      case fail: JsError =>
        println("couldn't parse json retrieved from api. Error: " + JsError.toJson(fail).toString())
        self ! PoisonPill
        lastSender.tell(FetchPokemonByIdResponse(lastRequest, null, RequestStatus.Error), self)
      case _ =>
        println("dunno, lol")
        lastSender.tell(FetchPokemonByIdResponse(lastRequest, null, RequestStatus.Error), self)
        self ! PoisonPill
    }
  }

  def onResultReceived(dataBytes: ByteString): Unit = {
    val body = dataBytes.decodeString(ByteString.UTF_8)
    println("Received result from pokeApi: " + body)
    ParseBody(body, false)
  }

  def waitingForResponse : Receive = {
    case HttpEntity.Strict(contentType,data) =>
      onResultReceived(data)
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