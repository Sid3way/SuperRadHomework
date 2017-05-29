package PokemonsDataStore

import java.io.FileReader

import Common.{FetchPokemonByIdRequest, FetchPokemonByIdResponse, GetStatsRequest, RequestStatus}
import PokemonsDataStore.PokemonModel.{PokemonStat, TypeRouter}
import akka.actor.{Actor, ActorRef, Props, Stash}
import java.nio.file.{Files, Paths}

import scala.io.Source
/**
  * Created by damie on 19/05/2017.
  */
class Pokedex(pokeApiClient : ActorRef) extends Actor with Stash{

  var pokemonsLeftToInit = 721

  def OnPokemonReceived(response : FetchPokemonByIdResponse): Unit = {
    response match {
      case FetchPokemonByIdResponse(_, _, RequestStatus.Success) =>
        // Spawning pokemon actor containing info retrieved from pokeApi
        pokemonsLeftToInit -= 1
        val currentPokemonId = 722 - pokemonsLeftToInit
        context.actorOf(Props(new PokemonBase(response.result, typeRouter)), response.result.name)
        if (pokemonsLeftToInit > 0) {
            println("Fetching next pokemon (id: " +  currentPokemonId + " )")
            CreatePokemon(currentPokemonId)
          }
        else {
          println("No more pokemon to spawn !")
          unstashAll()
          context.become(receive)
        }
      case FetchPokemonByIdResponse(request : FetchPokemonByIdRequest, _, RequestStatus.Error) =>
        // Trying again. Not the best way to handle a failure, only serves as fail-safe for now
        CreatePokemon(request.id)
    }
  }


  var typeRouter : ActorRef = ActorRef.noSender

  def CreatePokemon(id: Int): Unit = {
    pokeApiClient.tell(FetchPokemonByIdRequest(id), self)
  }

  override def preStart(): Unit = {
    super.preStart()
    typeRouter = context.actorOf(Props[TypeRouter], "typeRouter")
    CreatePokemon(1)
  }

  // I could have stashed request until all pokemons are up before answering but I prefer to be as reactive as possible,
  // even if it means only having partial results
  override def receive: Receive = {
    case request : GetStatsRequest =>
      val path = context.self.path + "/" + request.pokemonName
      println("Received request for pokemon named " + request.pokemonName + ". Searching at path: " + path)
      context.actorSelection(path).forward(request)
    case response : FetchPokemonByIdResponse =>
      OnPokemonReceived(response)
  }
}
