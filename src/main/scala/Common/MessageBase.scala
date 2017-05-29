package Common

import Common.RequestStatus.RequestStatus
import PokemonsDataStore.PokemonModel.{PokemonModel, PokemonStat, PokemonStatsDiff}

/**
  * Created by damie on 15/05/2017.
  */
trait MessageBase {
  val ApiVersion = 1
}

object RequestStatus extends Enumeration {
    type RequestStatus = Value
    val Success, Error = Value
}

// Requests
abstract class RequestBase() extends MessageBase
case class GetStatsRequest(pokemonName : String) extends RequestBase
case class FetchPokemonByIdRequest(id : Int) extends RequestBase
case class GetStatsComparisonRequest(id : Int) extends RequestBase
// Responses
abstract class ResponseBase(requestBase: RequestBase, status : RequestStatus) extends MessageBase
case class GetStatsResponse(requestBase: RequestBase, PokemonStats : List[PokemonStat], status : RequestStatus) extends ResponseBase(requestBase: RequestBase, status : RequestStatus)
case class FetchPokemonByIdResponse(requestBase: RequestBase, result : PokemonModel, status : RequestStatus) extends ResponseBase(requestBase: RequestBase, status : RequestStatus)
case class GetStatsComparisonResponse(requestBase: RequestBase, PokemonStatsDiff : PokemonStatsDiff, status : RequestStatus) extends ResponseBase(requestBase: RequestBase, status : RequestStatus)
//Notifications
case class PokemonAddedToSystem(pokemonModel: PokemonModel) extends MessageBase
