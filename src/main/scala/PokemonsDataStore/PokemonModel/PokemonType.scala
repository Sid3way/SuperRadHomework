package PokemonsDataStore.PokemonModel

import play.api.libs.json.{JsPath, Reads}

/**
  * Created by damie on 16/05/2017.
  */
case class PokemonType(pokemonType : String)

object PokemonType {
  implicit val pokemonTypeReads: Reads[PokemonType] = (JsPath \ "type" \ "name").read[String].map(PokemonType.apply)
}
