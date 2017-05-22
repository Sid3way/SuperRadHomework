package PokemonsDataStore

import Common.{FetchPokemonByIdRequest, FetchPokemonByIdResponse, GetStatsRequest, RequestStatus}
import PokemonsDataStore.PokemonModel.PokemonStats
import akka.actor.{Actor, ActorRef, Props, Stash}

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
        context.actorOf(Props(new PokemonBase(response.result)), response.result.name)
        if (pokemonsLeftToInit > 0)
          pokeApiClient.tell(FetchPokemonByIdRequest(pokemonsLeftToInit), self)
        else
          unstashAll()
          context.become(receive)
      case FetchPokemonByIdResponse(request, _, RequestStatus.Error) =>
        // Trying again. Not the best way to handle a failure, only serves as fail-safe for now
        pokeApiClient.tell(request, self)
    }
  }


  override def preStart(): Unit = {
    super.preStart()
    pokeApiClient.tell(FetchPokemonByIdRequest(pokemonsLeftToInit), self)
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
