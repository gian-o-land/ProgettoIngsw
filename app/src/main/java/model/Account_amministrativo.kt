package model

class Account_amministrativo(private var username: String,
                             private var password: String,
                             private var ruolo: D_ruolo,
                             private var adminCreatore: String?) {

    fun getUsername(): String{
        return username
    }

    fun setUsername(newName: String){
        username = newName
    }

    fun getPassword(): String{
        return password
    }

    fun setPassword(newPassword: String){
        password = newPassword
    }

    fun getRuolo(): D_ruolo{
        return ruolo
    }

    fun setRuolo(newRuolo: D_ruolo){
        ruolo = newRuolo
    }

    fun getAdmin_creatore(): String?{
        return adminCreatore
    }

    fun setAdmin_creatore(newCreatore: String?){
        adminCreatore = newCreatore
    }

}