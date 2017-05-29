import Common._
import PokeApiRequester.PokeApiClient
import PokemonsDataStore.{Pokedex, PokemonBase}
import PokemonsDataStore.PokemonModel.PokemonStat
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl.Flow

/**
  * Created by damie on 13/05/2017.
  */
object Bootstrapper extends App {

  override def main(a: Array[String]) = Initialize()

  def Initialize(): Unit ={
    // Do stuff
    val actorSystem = ActorSystem("Starburst")
    val pokeApiClient = actorSystem.actorOf(Props[PokeApiClient], name = "pokeApiClient")
    val pokedex = actorSystem.actorOf(Props(new Pokedex(pokeApiClient)), name="pokedex")
    val consoleWriter = actorSystem.actorOf(Props[ConsoleWriter], name = "consoleWriter")

    //pokedex.tell(GetStatsRequest("pikachu"), consoleWriter)
    //pokedex.tell(GetStatsRequest("cha*"), consoleWriter)
    //pokeApiClient.tell(FetchPokemonByIdRequest(13), consoleWriter)
  }
}

class ConsoleWriter extends Actor {
  override def receive: Receive =  {
    case GetStatsResponse(request : GetStatsRequest, stats : List[PokemonStat], _) =>
      println(s"Received stats for pokemon ${request.pokemonName}: " + stats)
  }
}

