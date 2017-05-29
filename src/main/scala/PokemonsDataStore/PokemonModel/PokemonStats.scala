package PokemonsDataStore.PokemonModel

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * Created by damie on 16/05/2017.
  */
case class PokemonStat(name : String, base_stat : Int)

object PokemonStat {
  implicit val pokemonStatReads: Reads[PokemonStat] = (
      (JsPath \ "stat" \ "name").read[String] and
      (JsPath \ "base_stat").read[Int]
    )(PokemonStat.apply _)
}

case class PokemonStatsDiff(statsDiff : List[PokemonStat], typeAverageStats : List[PokemonStat])
