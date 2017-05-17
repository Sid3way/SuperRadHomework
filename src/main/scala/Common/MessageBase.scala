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

// Responses
abstract class ResponseBase(requestBase: RequestBase) extends MessageBase
case class GetStatsResponse(PokemonStats : PokemonStats) extends MessageBase
