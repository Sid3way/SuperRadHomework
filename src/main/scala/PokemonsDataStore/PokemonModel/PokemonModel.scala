package PokemonsDataStore.PokemonModel

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._
/**
  * Created by Damie on 19/05/2017.
  */


case class PokemonModel(id : Int, name : String, base_experience : Int, height : Int, weight : Int, pokemonStats: List[PokemonStat], types : List[PokemonType])

object PokemonModel {
  implicit val pokemonReads: Reads[PokemonModel] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "base_experience").read[Int] and
      (JsPath \ "height").read[Int] and
      (JsPath \ "weight").read[Int] and
      (JsPath \ "stats").read[List[PokemonStat]] and
      (JsPath \ "types").read[List[PokemonType]]
    )(PokemonModel.apply _)
}