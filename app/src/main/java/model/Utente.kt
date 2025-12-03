package model

import model.source.DefaultPhonePrefix
import model.source.CommonPhonePrefixes
import model.source.PhonePrefix

class Utente(
    private var email: String,
    private var telefono: String,
    private var password: String,
    private var nomeCognome: String,
    private val selectedPrefix: PhonePrefix = DefaultPhonePrefix
){

    val fullPhoneNumber: String
        get() = "${selectedPrefix.prefix}${telefono}"

    fun getTelefono(): String{
        return telefono
    }

    fun setTelefono(newTelefono: String){
        telefono = newTelefono
    }

    fun getPassword(): String{
        return password
    }

    fun setPassword(newPassword: String){
        password = newPassword
    }

    fun getEmail(): String{
        return email
    }

    fun setEmail(newMail: String){
        email = newMail
    }

    fun getNomeCognome(): String{
        return nomeCognome
    }

    fun setNomeCognome(newNomeCognome: String){
        nomeCognome = newNomeCognome
    }
}