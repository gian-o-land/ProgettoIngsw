package model

class Agente(private var username: String, private var telefono: String, private var password: String, private var email: String, private var adminCreatore: String?){

    fun getUsername(): String{
        return username
    }

    fun setUsername(newName: String){
        username = newName
    }

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

    fun getAdmin_creatore(): String?{
        return adminCreatore
    }

    fun setAdmin_creatore(newCreatore: String?){
        adminCreatore = newCreatore
    }
}