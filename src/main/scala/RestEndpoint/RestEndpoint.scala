package RestEndpoint
/*


import Common.{FetchPokemonByIdRequest, FetchPokemonByIdResponse, RequestStatus}
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
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
import akka.http.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{Flow, Sink}
import akka.http.Http
import akka.stream.ActorFlowMaterializer

/**
  * Created by damie on 23/05/2017.
  */
class RestEndpoint {

  implicit val system = ActorSystem()
  implicit val materializer = ActorFlowMaterializer()

  val serverSource = Http(system).bind(interface = "localhost", port = 8080)
  serverSource.runForeach { connection => // foreach materializes the source
    println("Accepted new connection from " + connection.remoteAddress)
    // ... and then actually handle the connection
  }

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(
        entity = HttpEntity(MediaTypes.`text/html`, "<html><body>Hello world!</body></html>"))

    case HttpRequest(GET, Uri.Path("/ping"), _, _, _)  => HttpResponse(entity = "PONG!")
    case HttpRequest(GET, Uri.Path("/crash"), _, _, _) => sys.error("BOOM!")
    case _: HttpRequest                                => HttpResponse(404, entity = "Unknown resource!")
  }

  val bindingFuture = serverSource.to(Sink.foreach { connection =>
    println("Accepted new connection from " + connection.remoteAddress)

    connection handleWithSyncHandler requestHandler
    // this is equivalent to
    // connection handleWith { Flow[HttpRequest] map requestHandler }
  }).run()

}



*/