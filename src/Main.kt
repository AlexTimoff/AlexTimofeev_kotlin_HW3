import java.awt.SystemColor.menu
import java.lang.System.exit
import java.util.regex.Pattern
import kotlin.system.exitProcess

/*
Домашнее задание №3

Продолжаем дорабатывать домашнее задание из предыдущего семинара. За основу берём код решения из предыдущего домашнего задания.

— Измените класс Person так, чтобы он содержал список телефонов и список почтовых адресов, связанных с человеком.
— Теперь в телефонной книге могут храниться записи о нескольких людях. Используйте для этого наиболее подходящую структуру данных.
— Команда AddPhone теперь должна добавлять новый телефон к записи соответствующего человека.
— Команда AddEmail теперь должна добавлять новый email к записи соответствующего человека.
— Команда show должна принимать в качестве аргумента имя человека и выводить связанные с ним телефоны и адреса электронной почты.
— Добавьте команду find, которая принимает email или телефон и выводит список людей, для которых записано такое значение.

 */
fun main() {
    println("Введите команду соответсвующую команду или <help> для вывода списка команд на экран ")
    while (true) {
        val command: Command = readCommand()
        if (command.isValid()) {
            println(command)
            command.execute()
        } else {
            error()
            Command.Help()
        }
    }
}

data class Person(
    var name: String,
    var phones: MutableList<String> = mutableListOf(),
    var emails: MutableList<String> = mutableListOf()
) {
    override fun toString(): String {
        return buildString {
            append("Пользователь: ")
            append(name)
            if(phones.isNotEmpty()){
                append("\n\t")
                append("Номера телефонов: ")
                append(phones)
            }
            if(emails.isNotEmpty()){
                append("\n\t")
                append("Электронные адреса: ")
                append(emails)
            }
        }
    }
}

var allPhonebook= mutableMapOf<String,Person>()

sealed interface AllCommand{
    fun isValid(): Boolean
    fun execute()
}

//Объявляем sealed-класс Command, реализующий интерфес AllCommand. Внутри создаем соответсвующие классы комманд.
sealed class Command: AllCommand {

    class Help : Command() {
        override fun isValid(): Boolean {
            return true
        }
        override fun execute() {
            println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            println("help - вывод информации о доступных командах.")
            println("add <User> phone <UserPhone> - добавление контакта с номером телефона, " +
                        "а также добавление нового номера телефона к уже существующему контакту.")
            println("add <User> email <UserEmail> - добавление контакта с электронной почтой, " +
                        "а также добавление нового эл. адреса к уже существующему контакту.")
            println("show <User> - вывод списка телефонов и эл. адресов пользователя.")
            println("find <Contact> - вывод всех контактов по заданному номеру телефона или адресу эл. почты.")
            println("print - вывод всех сохраненных контактов.")
            println("exit - выход из приложения.")
            println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
        }
        override fun toString(): String {
            return "Вывод справочной информации о работе приложения"
        }

    }


    //Объявляем класс выхода из приложения
    class Exit : Command() {
        override fun isValid(): Boolean {
            return true
        }
        override fun execute() {
            println("До свидания!")
            exitProcess(0)
        }
        override fun toString(): String {
            return "Введена команда <exit>"
        }
    }


    // Создаем класс вывода контактов пользователя
    class Show (val name: String) : Command() {
        override fun isValid(): Boolean {
            return true
        }
        override fun execute() {
            if (allPhonebook.isEmpty()) {
                println("Телефонная книга пуста")
            } else if (allPhonebook.containsKey(name)) {
                println(allPhonebook[name])
            } else {
                println("Пользователь с именем $name не обнаружен")
            }
        }
        override fun toString(): String {
            return "Введена команда <show>"
        }
    }


    //Создаем класс поиска пользователей по номеру телефона или электронной почте
    class Find (val contact: String):Command(){
        override fun isValid(): Boolean {
            return true
        }
        override fun execute() {
            val people = mutableListOf<Person>()
            if (allPhonebook.isEmpty()) {
                println("Телефонная книга пуста")
            }else{
                for (person in allPhonebook.values){
                    if(person.phones.contains(contact) || person.emails.contains(contact)){
                        people.add(person)
                    }
                }
            }
            if (people.isEmpty()) {
                println("Пользователь с $contact не обнаружен")
            }else{
                for (person in people){
                    println(person)
                }
            }
        }

        override fun toString(): String {
            return "Введена команда <find>"
        }
    }


    //По личной инициативе добавляем класс вывода всей телефонной книги на экран(не требуется в ДЗ)
    class PrintAll:Command(){
        override fun isValid(): Boolean {
            return true
        }

        override fun execute() {
            for (person in allPhonebook.values) {
                println(person)
                println()
            }
        }
        override fun toString(): String {
            return "Введена команда <print>"
        }
    }


    // Создаем класс добавления номера телефона с проверкой
    class AddPhone(val name: String, val phone: String) : Command() {
        val phonePattern1 = Regex("[+]+\\d+")
        val phonePattern2 = Regex("\\d+")
        override fun isValid(): Boolean {
            return phone.matches(phonePattern1) || phone.matches(phonePattern2)
        }

        override fun execute() {
            if(allPhonebook.containsKey(name)){
                allPhonebook[name]?.phones?.add(phone)
            }
            else{
                val person=Person(
                    name,
                    phones= mutableListOf(phone)
                )
                allPhonebook.put(name,person)
            }
        }

        override fun toString(): String {
            return "Введена команда добавления нового пользователя ${name} с номером телефона ${phone}"
        }
    }


    // Создаем класс добавления электронной почты с проверкой
    class AddEmail(val name: String, val email: String) : Command() {
        val emailPattern = Regex("[a-zA-z0-9]+@[a-zA-z0-9]+[.]([a-zA-z0-9]{2,4})")
        override fun isValid(): Boolean {
            return email.matches(emailPattern)
        }

        override fun execute() {
            if(allPhonebook.containsKey(name)){
                allPhonebook[name]?.emails?.add(email)
            }
            else{
                val person=Person(
                    name,
                    emails= mutableListOf(email)
                )
                allPhonebook.put(name,person)
            }
        }

        override fun toString(): String {
            return "Введена команда добавления нового пользователя ${name} с электронной почтой ${email}"
        }
    }

}


fun readCommand(): Command {
    print (">>>>")
    var userInput: String = ""
    userInput = readLine().toString()
    val words: List <String> = userInput.split(' ')
    return when (words[0]){
        "add"->{
            if(words.size==4 &&words[2].contains("phone")){
                Command.AddPhone(words[1],words[3])
            }
            else if (words.size==4 &&words[2].contains("email")) {
                Command.AddEmail(words[1], words[3])
            }
            else{
                error()
                return Command.Help()
            }
        }
        "help"->{
            if(words.size==1){
                Command.Help()
            }
            else{
                error()
                return Command.Help()
            }
        }
        "print"->{
            if(words.size==1){
                Command.PrintAll()
            }
            else{
                error()
                return Command.Help()
            }
        }

        "exit"->{
            if(words.size==1){
                Command.Exit()
            }
            else{
                error()
                return Command.Help()
            }
        }
        "show"->{
            if(words.size==2){
                Command.Show(words[1])
            }
            else{
                error()
                return Command.Help()
            }
        }

        "find"->{
            if(words.size==2){
                Command.Find(words[1])
            }
            else{
                error()
                return Command.Help()
            }
        }

        else->{
            error()
            return Command.Help()
        }
    }
}


fun error(){
    println("Введена некорректная команда! Попробуйте снова!")
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
}



