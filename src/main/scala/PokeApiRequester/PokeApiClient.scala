package PokeApiRequester

import Common.{FetchPokemonByIdRequest, FetchPokemonByIdResponse}
import akka.actor.{Actor, ActorLogging, ActorRef, Stash}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString

import HttpMethods._

import scala.util.{Failure, Success}

/**
  * Created by Damie on 17/05/2017.
  */
class PokeApiClient extends Actor with Stash{

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)

  var lastSender : ActorRef = ActorRef.noSender
  var lastRequest = FetchPokemonByIdRequest(0)

  def RequestApi(request : FetchPokemonByIdRequest): Unit = {
    lastSender = sender
    lastRequest = request
    context.become(waitingForResponse)
    val pokeApiUrl = "http://pokeapi.co/api/v2/pokemon/" + request.id + "/"
    //val result = scala.io.Source.fromURL(pokeApiUrl).mkString
    val result = http.singleRequest(HttpRequest(GET, uri = pokeApiUrl))
  }

  def onResultReceived() : Unit = {
      lastSender.tell(FetchPokemonByIdResponse(lastRequest, "toBeReplaced"), self)
      unstashAll()
      context.unbecome()
    }

  def waitingForResponse : Receive = {
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      println("Received result from pokeApi")
      onResultReceived()
    case Failure(_) =>
      println("Shit's on fire, yo")
      onResultReceived()
    case _ => stash()
  }

  override def receive: Receive = {
    case request : FetchPokemonByIdRequest =>
      RequestApi(request)
  }
}

