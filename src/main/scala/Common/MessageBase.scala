package Common

import PokemonsDataStore.PokemonTraits.PokemonStats

/**
  * Created by damie on 15/05/2017.
  */
trait MessageBase {
  val ApiVersion = 1
}


// Requests
abstract class RequestBase() extends MessageBase
case class GetStatsRequest() extends RequestBase
case class FetchPokemonByIdRequest(id : Int) extends RequestBase

// Responses
abstract class ResponseBase(requestBase: RequestBase) extends MessageBase
case class GetStatsResponse(requestBase: RequestBase, PokemonStats : PokemonStats) extends ResponseBase(requestBase: RequestBase)
case class FetchPokemonByIdResponse(requestBase: RequestBase, result : String) extends ResponseBase(requestBase: RequestBase)
