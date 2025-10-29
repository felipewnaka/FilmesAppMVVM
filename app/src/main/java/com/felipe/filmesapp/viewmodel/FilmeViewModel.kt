package com.felipe.filmesapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.felipe.filmesapp.model.Validacao
import com.felipe.filmesapp.model.entity.Filme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FilmeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("filmes_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    var listaFilmes = mutableStateOf(listOf<Filme>())
        private set

    init {
        carregarFilmes()
    }

    private fun carregarFilmes() {
        val json = prefs.getString("filmes_lista", null)
        if (!json.isNullOrEmpty()) {
            val tipo = object : TypeToken<List<Filme>>() {}.type
            listaFilmes.value = gson.fromJson(json, tipo)
        }
    }

    private fun salvarLocalmente() {
        val json = gson.toJson(listaFilmes.value)
        prefs.edit().putString("filmes_lista", json).apply()
    }

    fun salvarFilme(titulo: String, diretor: String): String {
        if (Validacao.haCamposEmBranco(titulo, diretor)) {
            return "Preencha todos os campos!"
        }

        val filme = Filme(
            Validacao.getId(),
            titulo,
            diretor
        )

        listaFilmes.value += filme
        salvarLocalmente()
        return "Filme salvo com sucesso!"
    }

    fun excluirFilme(id: Int) {
        listaFilmes.value = listaFilmes.value.filter { it.id != id }
        salvarLocalmente()
    }

    fun atualizarFilme(id: Int, titulo: String, diretor: String): String {
        if (Validacao.haCamposEmBranco(titulo, diretor)) {
            return "Ao editar, preencha todos os dados do filme!"
        }

        val filmesAtualizados = listaFilmes.value.map { filme ->
            if (filme.id == id) {
                filme.copy(titulo = titulo, diretor = diretor)
            } else {
                filme
            }
        }

        listaFilmes.value = filmesAtualizados
        salvarLocalmente()
        return "Filme atualizado!"
    }
}