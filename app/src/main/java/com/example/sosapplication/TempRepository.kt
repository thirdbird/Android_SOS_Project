package com.example.sosapplication

val tempRepository = TempRepository().apply {

    addToDo(
        "Anders Andersson",
        "Familj"
    )
    addToDo(
        "Abbot Heizman",
        "Familj"
    )
    addToDo(
        "Jean Clark",
        "Familj"
    )
    addToDo(
        "Meredith O'Lark",
        "Familj"
    )
    addToDo(
        "Henry Hotshot",
        "Familj"
    )
    addToDo(
        "Bobby Pin",
        "Familj"
    )

}

class TempRepository {
    private val toDos = mutableListOf<ToDo>()

    fun addToDo(name: String, content: String): Int{
        val id = when {
            toDos.count() == 0 -> 1
            else -> toDos.last().id+1
        }
        toDos.add(ToDo(
            id,
            name,
            content
        ))
        return id
    }

    fun getAllToDos() = toDos

    fun getToDoById(id: Int) =
        toDos.find {
            it.id == id
        }

    fun deleteToDoById(id: Int) =
        toDos.remove(
            toDos.find {
                it.id == id
            }
        )

    fun updateToDoById(id: Int, newName: String, newContent: String){
        getToDoById(id)?.run{
            name = newName
            content = newContent
        }
    }
}