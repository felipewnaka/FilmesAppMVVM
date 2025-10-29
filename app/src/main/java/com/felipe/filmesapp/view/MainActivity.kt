package com.felipe.filmesapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipe.filmesapp.viewmodel.FilmeViewModel
import androidx.compose.runtime.Composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var titulo by remember { mutableStateOf("") }
    var diretor by remember { mutableStateOf("") }
    var id by remember { mutableStateOf(0) }
    var textoBotao by remember { mutableStateOf("Salvar") }
    var modoEditar by remember { mutableStateOf(false) }

    val filmeViewModel: FilmeViewModel = viewModel()
    var listaFilmes by filmeViewModel.listaFilmes
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var mostrarCaixaDialogo by remember { mutableStateOf(false) }

    if (mostrarCaixaDialogo) {
        ExcluirFilme(
            onConfirm = {
                filmeViewModel.excluirFilme(id)
                mostrarCaixaDialogo = false
            },
            onDismiss = { mostrarCaixaDialogo = false }
        )
    }

    Column(
        Modifier.fillMaxSize().padding(20.dp)
    ) {
        Text(text = "Lista de Filmes", modifier = Modifier.fillMaxWidth(), fontSize = 22.sp)
        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = titulo,
            onValueChange = { titulo = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Título do filme") }
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = diretor,
            onValueChange = { diretor = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Diretor do filme") }
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            val retorno = if (modoEditar) {
                modoEditar = false
                textoBotao = "Salvar"
                filmeViewModel.atualizarFilme(id, titulo, diretor)
            } else {
                filmeViewModel.salvarFilme(titulo, diretor)
            }
            Toast.makeText(context, retorno, Toast.LENGTH_LONG).show()
            titulo = ""
            diretor = ""
            focusManager.clearFocus()
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = textoBotao)
        }

        Spacer(modifier = Modifier.height(15.dp))

        LazyColumn {
            items(listaFilmes) { filme ->
                Text("${filme.titulo} (${filme.diretor})", modifier = Modifier.fillMaxWidth(), fontSize = 18.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    Button(onClick = {
                        id = filme.id
                        mostrarCaixaDialogo = true
                    }) { Text("Excluir") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = {
                        modoEditar = true
                        id = filme.id
                        titulo = filme.titulo
                        diretor = filme.diretor
                        textoBotao = "Atualizar"
                    }) { Text("Atualizar") }
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

@Composable
fun ExcluirFilme(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar exclusão") },
        text = { Text("Tem certeza que deseja excluir este filme?") },
        confirmButton = { Button(onClick = onConfirm) { Text("Sim, excluir") } },
        dismissButton = { Button(onClick = onDismiss) { Text("Não, cancelar") } }
    )
}