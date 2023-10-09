package com.example.agroagil.Login.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.agroagil.R
import com.example.agroagil.VariablesFuncionesGlobales
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun MensajeDialogoNohayNavegacionDefinida(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "Error de navegación") },
            text = {
                Text(
                    text = "El programador no ha indicado la navegación indicada, por favor consulte con el soporte técnico.",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                        onAccept()
                    }
                ) {
                    Text(text = "Aceptar")
                }
            }
        )
    }
}


@Composable
fun ScreenDeBienvenida(onNavigate: (NavigationInicio) -> Unit) {

    //esto es por si falto definir alguna navegacion
    var mostrarNoDefinidaNavegacion by remember { mutableStateOf(false) }

    // Evita que el usuario haga algo en la aplicación
    BackHandler(enabled = false, onBack = {})
    // Manejar la navegación después de esos 3 segundos.
    LaunchedEffect(true) {
        //ni bien se forma el composable
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({

            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (currentUser != null) {
                // El usuario está autenticado, verifica si su perfil está completo
                val userReference = FirebaseDatabase.getInstance().getReference("usuarios")
                    .child(currentUser.uid)

                userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // El perfil del usuario está completo, redirige a la pantalla de inicio
                            onNavigate(NavigationInicio.PantallaMenu)
                        } else {
                            // El usuario estaba autenticado pero sin cargar sus datos en la firebase realtime database
                            auth.signOut()
                            onNavigate(NavigationInicio.PantallaLogin)
                            //se cierra sesion y va a la página de login
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Manejar errores si es necesario o se deja vacio
                    }
                }
                )
            } else {
                // El usuario no está autenticado, redirige a la pantalla de inicio de sesión
                onNavigate(NavigationInicio.PantallaLogin)

            }
        }, //tiempo que se muestra el logo AA en pantalla
            3000)


    }

    //para que la imagen no se deforme siempre que este dentro del LazyColumn
    //obtiene el ancho de la pantalla actual en píxeles
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    // ancho original deseado de la imagen en dp.
    val imageWidth = 329.dp
    // se define la altura original deseada de la imagen en dp.
    val imageHeight = 224.dp
    //calcula el nuevo ancho deseado para la imagen como la mitad del ancho de la pantalla actual
    val targetImageWidth = screenWidth / 2
    // dará la escala que debes aplicar a la imagen para que se ajuste correctamente en el espacio disponible sin deformarse.
    val scaleFactor = targetImageWidth / imageWidth


    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //imagen logo AA
        item {
            Image(
                painter = painterResource(id = R.drawable.logo_aa),
                contentDescription = "Image",
                modifier = Modifier
                    .width(targetImageWidth)
                    .height(imageHeight * scaleFactor),
                contentScale = ContentScale.Crop
            )
        }
        //nombre AgroAgil
        item {
            Text(
                text = "AGROÁGIL",
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }


    }

    //toma variable global, es por si aparece una nueva navegación y se han olvidado de agregarla
    if (!VariablesFuncionesGlobales.navegacionDefinida) {
        MensajeDialogoNohayNavegacionDefinida(
            showDialog = mostrarNoDefinidaNavegacion,
            onDismiss = { mostrarNoDefinidaNavegacion = false },
            onAccept = {
                //por ahora no hace nada
            }
        )

    }


}

@Composable
fun IniciarSesionGoogle(
    loginGoogleViewModel: LoginGoogleViewModel,
    registrateConGoogle:() -> Unit,
    andaAlMenu: ()-> Unit
) {
    //esto es por si falto definir alguna navegación
    var mostrarNoDefinidaNavegacion by remember { mutableStateOf(false) }

    var errorAutenticacion by rememberSaveable { mutableStateOf(false) }
    var celularSinInternet by remember { mutableStateOf(false) }

    //el ID de cliente web esta en el proyectro agroagil de Firebase, con él se permite que los usuarios inicien sesión en la app utilizando sus cuentas de Google.
    val token = "989896107665-5h9gkbt15gn5b5fk7imcia4sn7a43rhs.apps.googleusercontent.com"
    //se obtiene contexto actual de la aplicación
    val context = LocalContext.current
    //se utilizará para iniciar una actividad y recibir el resultado cuando esa actividad finalice.
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        //funcion lambda que se ejecutará cuando se obtenga un resultado.
        // después de que el usuario intente iniciar sesión con Google.

        val isConnected = VariablesFuncionesGlobales.isConnected(context)
        //se valida si el celular tiene internet
        if (!isConnected) {
            // Mostrar error si no hay conexión a Internet
            celularSinInternet = true // Actualizado
            // Mostrar error si no hay conexión a Internet
            //  showError("El celular no tiene acceso a Internet")
            return@rememberLauncherForActivityResult
        }
        else{
            //aca se marca que tiene
            celularSinInternet = false
        }
        /* GoogleSignIn es una clase que se utiliza para realizar operaciones relacionadas con la autenticación de Google,
        como iniciar sesión con cuentas de Google.Obtiene información sobre la cuenta, como el nombre, la dirección de correo electrónico
         y un identificador único de usuario.
        getSignedInAccountFromIntent: extraer la cuenta de Google, recibiendo información sobre el resultado del inicio de sesión.
        */
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            /*
        obtiene el resultado y la cuenta de Google que el usuario ha utilizado para iniciar sesión.
        se crea una credencial de autenticación de Firebase utilizando el token de identificación para las cuentas de Google.
        La credencial se utiliza para autenticar al usuario con Firebase Authentication.
        account.idToken es un token de seguridad generado por Google que se utiliza para verificar la identidad del usuario.
            * */
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            // La variable credential (AuthCredential) almacena credenciales de autenticación, en este caso, de Google.
            // GoogleAuthProvider es una clase de Firebase Authentication que crea credenciales de autenticación específicas de Google.
            // //getCredential(account.idToken, null) es un método de GoogleAuthProvider utilizando el token de ID de Google
            loginGoogleViewModel.signInWithGoogleCredentials(credential,registrateConGoogle,andaAlMenu)
        }
        catch (ex: Exception) {
            //esto es por las dudas de que haya un error, por ahora nunca paso por aca
            Log.d("Autenticacion", "El error fue  ${ex.localizedMessage}")
            //este es para que me muestre un mensaje de error en la autenticacion por las dudas, pero nunca ocurrio por ahora
            errorAutenticacion=true

        }

    }

    if (celularSinInternet) {
        // Mostrar el mensaje de error si no hay conexión a Internet
        Text(text = "El celuar no tiene conexión a internet", color=Color.Red)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentWidth() //el ancho del elemento se adaptará al contenido que contiene.
            .padding(5.dp)
            .clip(RoundedCornerShape(10.dp)) //esquinas un poquito redondeadas
            .clickable {

                val options = GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(token)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, options)
                launcher.launch(googleSignInClient.signInIntent)

            }
            .border( //le da color y ancho al borde
                1.dp,
                Color.Gray,
                RoundedCornerShape(10.dp)
            )
    ) {
        Image(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = "Iniciar sesión con Google",
            modifier = Modifier
                .padding(10.dp)
                .size(40.dp)
        )
        Text(
            text = "Iniciar sesión con Google",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(5.dp)
        )
    }

    if(errorAutenticacion){
        //aca mostrar por si hay problemas en la autenticación
        Dialog(
            onDismissRequest = {errorAutenticacion=false},
            content = {
                // mensaje de error y cerrar
                Text(text = "se produjo un error al intentar iniciar sesión con su cuenta de google, intentelo de nuevo")
                Button(onClick = { errorAutenticacion = false },
                    modifier =Modifier.fillMaxWidth()
                ) {
                    Text( text = "cerrar")
                }
            }
        )

    }

    // Diálogo de espera , el circular
    if (loginGoogleViewModel.isLoadingDialogVisible.value) {
        Dialog(
            onDismissRequest = {}
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }

    //toma variable global, es por si aparece una nueva navegación y se han olvidado de agregarla
    if (!VariablesFuncionesGlobales.navegacionDefinida) {
        MensajeDialogoNohayNavegacionDefinida(
            showDialog = mostrarNoDefinidaNavegacion,
            onDismiss = { mostrarNoDefinidaNavegacion = false },
            onAccept = {
                //por ahora no hace nada
            }
        )

    }
}

@Composable
fun LogoAA() {
    //para que la imagen AA no se deforme
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val imageWidth = 329.dp
    val imageHeight = 224.dp
    //es la AA más chica que la de inicio
    val targetImageWidth = (screenWidth / 4).coerceAtLeast(160.dp)
    val scaleFactor = targetImageWidth / imageWidth

    Spacer(modifier = Modifier.padding(10.dp))
//logo AA
    Image(
        painter = painterResource(id = R.drawable.logo_aa),
        contentDescription = "Image",
        modifier = Modifier
            .width(targetImageWidth)
            .height(imageHeight * scaleFactor),
        contentScale = ContentScale.Crop // se ajusta para llenar el contenedor
    )
//titulo AgroAgil
    Text(
        text = "AGROÁGIL",
        modifier = Modifier.padding(20.dp),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModelLogin: LoginViewModel,
    viewModelLoginGoogle: LoginGoogleViewModel,
    auth: FirebaseAuth,
    onNavigate: (NavigationInicio) -> Unit
) {

    //esto es por si falto definir alguna navegacion
    var mostrarNoDefinidaNavegacion by remember { mutableStateOf(false) }

// Campos de entrada para correo electrónico y contraseña
    var email by rememberSaveable { viewModelLogin.email }
    var password by rememberSaveable { viewModelLogin.password }

    // esta vacio email
    var sinCorreo by rememberSaveable { mutableStateOf(true) }
    //esta vacia contraseña
    var sinContrasenia by rememberSaveable { mutableStateOf(true) }
    //mostrar contrasenia
    var mostrarContrasenia by rememberSaveable { mutableStateOf(false) }
    //apreta para login sin credenciales
    var sinCredencialesLogin by rememberSaveable { mutableStateOf(false) }
    //para validar si hay internet o no en el celular
    var sinInternetCelular by rememberSaveable { mutableStateOf(false) }
    var cerrarMensajesViewModel by rememberSaveable { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState()
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        //logo
        LogoAA()
//correo electronico
        Text(
            text = "Correo Electrónico",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = email,
            onValueChange = {
                email = it
                sinCorreo = it.isEmpty()
            },
            placeholder = {
                Text("miCorreo@ejemplo.com")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        if (sinCorreo) {
            Text(text = "*Debe escribir un correo electrónico", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

//contraseña
        Text(
            text = "Contraseña",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = password,
            onValueChange = {
                password = it
                sinContrasenia = it.isEmpty()
            },
            placeholder = {
                Text(text = "abc987!")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            //para que la contraseña se vea con circulos o asterisco
            visualTransformation = if (mostrarContrasenia) VisualTransformation.None else PasswordVisualTransformation(),
            //cuando se muestra contraseña y cuando no
            trailingIcon = {
                IconButton(
                    onClick = { mostrarContrasenia = !mostrarContrasenia },
                ) {
                    Icon(
                        imageVector = if (mostrarContrasenia) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (mostrarContrasenia) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        )

        if (sinContrasenia) {
            Text(text = "*Debe escribir una contraseña", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(40.dp))
        val context = LocalContext.current
//iniciar sesion
        Button(
            onClick = {

                if (sinContrasenia || sinCorreo) {
                    sinCredencialesLogin = true
                } else {
                    sinCredencialesLogin = false
                    val isConnected = VariablesFuncionesGlobales.isConnected(context)
                    if (!isConnected) {
                        sinInternetCelular = true
                    } else {
                        sinInternetCelular=false
                        viewModelLogin.iniciarSesion(auth, onNavigate, context)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)

        ) {
            Text(text = "Iniciar Sesión")
        }

        if (sinCredencialesLogin) {
            Text(
                text = " *Debe ingresar contraseña y correo electrónico para poder iniciar sesión",
                color = Color.Red
            )
        }

        if (sinInternetCelular) {
            cerrarMensajesViewModel = true
            Text(text = "El celuar no tiene conexión a internet",color=Color.Red)
        }

        if (!sinContrasenia && !sinCorreo) {
            // Mostrar el mensaje de error si existe
            viewModelLogin.mensajeError.value?.let { mensaje ->
                Text(
                    text =
                    if (mensaje == "The email address is badly formatted.") {
                        "el correo electronico que ha ingresado no es válido"
                    } else {
                        if (mensaje == "An internal error has occurred. [ INVALID_LOGIN_CREDENTIALS ]") {
                            "correo electronico y contraseña no encontrados, vuelva a escribirlos e intente iniciar sesion de nuevo"
                        } else mensaje
                    },
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onNavigate(NavigationInicio.PantallaRegistro)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Registrarse")
        }

        Spacer(modifier = Modifier.height(16.dp))

        IniciarSesionGoogle(viewModelLoginGoogle, {onNavigate(NavigationInicio.PantallaRegistroGoogle)}, {onNavigate(NavigationInicio.PantallaMenu)})

    }

//FIN DE LA COLUMNA
    Spacer(modifier = Modifier.height(30.dp))

    // Diálogo de espera
    if (viewModelLogin.isLoadingDialogVisible.value) {
        Dialog(
            onDismissRequest = {
            }
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }

    //toma variable global, es por si aparece una nueva navegación y se han olvidado de agregarla
    if (!VariablesFuncionesGlobales.navegacionDefinida) {
        MensajeDialogoNohayNavegacionDefinida(
            showDialog = mostrarNoDefinidaNavegacion,
            onDismiss = { mostrarNoDefinidaNavegacion = false },
            onAccept = {
                //por ahora no hace nada
            }
        )

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroConCuentaGoogle(viewModelRegistro: LoginGoogleViewModel, onNavigate: (NavigationInicio) -> Unit ) {

    //esto es por si falto definir alguna navegacion
    var mostrarNoDefinidaNavegacion by remember { mutableStateOf(false) }

    //datos
    var nombre by rememberSaveable { viewModelRegistro.nombre }
    var apellido by rememberSaveable { viewModelRegistro.apellido }
    var rol by rememberSaveable { viewModelRegistro.rol }

    // esta vacio
    var sinNombre by rememberSaveable { mutableStateOf(true) }
    var sinApellido by rememberSaveable { mutableStateOf(true) }
    var sinRolSeleccionado by rememberSaveable { mutableStateOf(true) }


    // Variable para controlar la visibilidad del diálogo de carga
    var falloRegistroDeUsuarioEnFirebaseRealtime by rememberSaveable { mutableStateOf(false) }
    var mensajeDeError by rememberSaveable { mutableStateOf("") }
    var mostrarGraficoDeEspera by rememberSaveable { mutableStateOf(false) }

    var sinCredencialesLogin by rememberSaveable { mutableStateOf(false) }

    var sinInternetCelular by rememberSaveable { mutableStateOf(false) }

    var cerrarMensajesViewModel by rememberSaveable { mutableStateOf(false) }

    var preguntarSalir by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState()
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        //logo
        LogoAA()
        //texto de bienvenida
        Text(
            text = "¡Bienvenido!",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(5.dp)
        )
        //indicación de qué debe hacer
        Text(
            text = "Por favor, completar con sus datos",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(5.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        //nombre
        Text(
            text = "Nombre",
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = nombre,
            onValueChange = {
                nombre = it
                sinNombre = it.isEmpty()
            },
            placeholder = {
                Text(" Juan Pedro")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        //valida que haya ingresado nombre
        if (sinNombre) {
            Text(text = "*Debe escribir su nombre", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //apellido
        Text(
            text = "Apellido",
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = apellido,
            onValueChange = {
                apellido = it
                sinApellido = it.isEmpty()
            },
            placeholder = {
                Text(" Rodríguez")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        //valida que apellido no este vacio
        if (sinApellido) {
            Text(text = "*Debe escribir su apellido", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Rol
        /* 4 estados estaOpcionPorDefecto, expandirSelector, */

        var estaOpcionPorDefecto by rememberSaveable { mutableStateOf(true) }
        var expandirSelector by rememberSaveable { mutableStateOf(false) }
        val listaDeRoles = listOf("Administrador del Campo", "Trabajador")

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //indicador de que debe selleccionar rol
            Text(
                text = "Rol",
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(5.dp)
            )
            // en este componente se a ver la opción que se elige
            OutlinedTextField(
                trailingIcon = { //flechita para arriba o para abajo según corresponda en el selector
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirSelector) },
                value = rol, //la opcion seleccionada
                onValueChange = { nuevoRolSeleccionado ->
                    rol = nuevoRolSeleccionado
                },
                enabled = false, // con esto no podes escribir un rol
                readOnly = true, // solo permite su lectura
                label = {
                    if (estaOpcionPorDefecto) {
                        //valida que se seleccione un rol y que no sea el por defecto
                        Text("Selecciona una opción")
                        sinRolSeleccionado = true
                    }
                },
                modifier = Modifier
                    .clickable {
                        estaOpcionPorDefecto = false
                        //se expande el selector
                        expandirSelector = true
                    }

                    .fillMaxWidth()


            )

            if (!estaOpcionPorDefecto) {
                DropdownMenu(
                    expanded = expandirSelector,
                    onDismissRequest = {
                        expandirSelector = false
                        if (rol.isEmpty()) {
                            //si no selecciono un rol esta la opcion por defecto
                            estaOpcionPorDefecto = true
                        } else {
                            sinRolSeleccionado = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listaDeRoles.forEach { rolSeleccionado ->
                        DropdownMenuItem(
                            text = {  Text( text = rolSeleccionado )},
                            onClick = { expandirSelector = false
                                rol = rolSeleccionado
                                sinRolSeleccionado = rol == "" || rol.isEmpty()
                            })
                    }
                }

            }
        }

        //valida que haya seleccionado un rol
        if (sinRolSeleccionado) {
            Text(text = "*Debe elegir su rol", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //correo electronico que viene de la autenticacion
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val correoText = currentUser?.email ?: ""

        Text(
            text = "Correo electrónico",
            modifier = Modifier.padding(5.dp)
        )
        OutlinedTextField(
            value = correoText,
            onValueChange = {},
            enabled = false, // el usuario ve no modifica
            readOnly = true, // solo permite su lectura
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current

        Button(
            onClick = {

                // Validar los datos ingresados
                if (sinNombre || sinApellido || sinRolSeleccionado) {
                    sinCredencialesLogin = true
                } else {
                    sinCredencialesLogin = false
                    //aca validar si hay internet sino se queda esperando mucho
                    val isConnected = VariablesFuncionesGlobales.isConnected(context)
                    if (!isConnected) {
                        sinInternetCelular = true
                        return@Button
                    } else {
                        sinInternetCelular = false

                        // Mostrar el diálogo de espera
                        mostrarGraficoDeEspera = true

                        val usuarioActual = FirebaseAuth.getInstance().currentUser
                        //se obtiene el usuario autenticado
                        if (usuarioActual != null) {
                            //se valida que esta autenticado y se va a hacer el registro
                            val userReference =
                                FirebaseDatabase.getInstance().getReference("usuarios")
                                    .child(usuarioActual.uid)

                            // se crear un objeto para los datos del usuario
                            val userData = mapOf(
                                "nombre" to nombre,
                                "apellido" to apellido,
                                "correoElectronico" to usuarioActual.email,
                                "rol" to rol
                            )

                            // se regsitra usuario en Firebase Realtime Database
                            userReference.setValue(userData)
                                .addOnSuccessListener {
                                    // Registro exitoso
                                    mostrarGraficoDeEspera = false
                                    // Redirigir al usuario a la pantalla de inicio, donde esta el menú
                                    onNavigate(NavigationInicio.PantallaMenu)
                                }
                                .addOnFailureListener { e ->
                                    // Error en el registro, se va a mostrar un mensaje
                                    mostrarGraficoDeEspera = false
                                    falloRegistroDeUsuarioEnFirebaseRealtime = true
                                    mensajeDeError = "Error al registrar el usuario: ${e.message}"
                                }
                        } else {
                            // El usuario no está autenticado
                            // se podria mostrar un mensaje
                        }

                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Registrarse")
        }

        //le falta algún dato
        if (sinCredencialesLogin) {
            Text(
                text = " *Debe ingresar nombre,apellido y seleccionar un rol para poder registrarse e iniciar sesión",
                color = Color.Red
            )
        }

        //le falta internet al celular
        if (sinInternetCelular) {
            cerrarMensajesViewModel = true
            Text(text = "El celuar no tiene conexión a internet", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        //salir sin registrarse
        Button(
            onClick = {
                preguntarSalir = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Salir sin registrarse")
        }

        // Mostrar el diálogo de carga si showDialog es true
        if (mostrarGraficoDeEspera) {
            Dialog(
                onDismissRequest = {},
                content = {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                }
            )
        }

        // Mostrar el diálogo de error si showDialogError es true
        if (falloRegistroDeUsuarioEnFirebaseRealtime) {
            Dialog(
                onDismissRequest = { falloRegistroDeUsuarioEnFirebaseRealtime = false },
                content = {
                    // mensaje de error y cerrar
                    Text(text = mensajeDeError)
                    Button(
                        onClick = { falloRegistroDeUsuarioEnFirebaseRealtime = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "cerrar")
                    }
                }
            )
        }

        // salir sin registro
        // Mostrar el cuadro de confirmación para "Salir sin hacer el registro"
        //se podría mejorar visualmente
        if (preguntarSalir) {
            Dialog(
                onDismissRequest = {},
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Text(
                                text = "Salir sin hacer el registro",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Text(
                                text = "¿Está seguro de que desea salir sin hacer el registro? Sus datos no serán guardados y volverá a la pantalla de inicio de sesión.",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // Botón salir sin hacer registro
                            Button(
                                onClick = {
                                    auth.signOut()
                                    preguntarSalir = false
                                    // Puedes redirigir al usuario a la pantalla de inicio de sesión
                                    onNavigate(NavigationInicio.PantallaLogin)
                                },
                            ) {
                                Text(text = "Sí, salir sin hacer el registro")
                            }
                            //Botón seguir con registro
                            Button(
                                onClick = {
                                    preguntarSalir = false
                                },
                            ) {
                                Text(text = "No salir, quiero continuar con mi registro")
                            }

                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //toma variable global, es por si aparece una nueva navegación y se han olvidado de agregarla
        if (!VariablesFuncionesGlobales.navegacionDefinida) {
            MensajeDialogoNohayNavegacionDefinida(
                showDialog = mostrarNoDefinidaNavegacion,
                onDismiss = { mostrarNoDefinidaNavegacion = false },
                onAccept = {
                    //por ahora no hace nada
                }
            )

        }


    }
}

//PENDIENTE?
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    loginViewModel: LoginViewModel,
    auth: FirebaseAuth,
    onNavigate: (NavigationInicio) -> Unit
) {

    //esto es por si falto definir alguna navegacion
    var mostrarNoDefinidaNavegacion by remember { mutableStateOf(false) }

// Campos de entrada para correo electrónico y contraseña

    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var correoElectronico by rememberSaveable { mutableStateOf("") }
    var correoElectronicoRepeticion by rememberSaveable { mutableStateOf("") }
    var contrasenia by rememberSaveable { mutableStateOf("") }
    var contraseniaRepeticion by rememberSaveable { mutableStateOf("") }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val imageWidth = 329.dp
    val imageHeight = 224.dp
    val targetImageWidth = screenWidth / 2
    val scaleFactor = targetImageWidth / imageWidth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState()
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.padding(40.dp))
        Image(
            painter = painterResource(id = R.drawable.logo_aa),
            contentDescription = "Image",
            modifier = Modifier
                .width(targetImageWidth)
                .height(imageHeight * scaleFactor),
            contentScale = ContentScale.Crop // se ajusta para llenar el contenedor
        )

        Text(
            text = "AGROÁGIL",
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(20.dp))

//nombre
        Text(
            text = "Nombre",
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = nombre,
            onValueChange = {
                nombre = it
            },
            placeholder = {
                Text(" Juan Pedro")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

//apellido
        Text(
            text = "Apellido",
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = apellido,
            onValueChange = {
                apellido = it
            },
            placeholder = {
                Text(" Rodríguez")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        var opcionPorDefecto by rememberSaveable { mutableStateOf(true) }
        var rolSeleccionado by rememberSaveable { mutableStateOf("") }
        var expandir by rememberSaveable { mutableStateOf(false) }
        val listaDeRoles = listOf("Administrador del Campo", "Trabajador")

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Rol",
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(5.dp)
            )
            //selector de roles
            OutlinedTextField(
                //flechita del selector
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandir) },
                value = rolSeleccionado,
                onValueChange = { nuevoRolSeleccionado ->
                    rolSeleccionado = nuevoRolSeleccionado
                },
                enabled = false, // no puedo modificar
                readOnly = true, // solo permite su lectura
                label = {
                    if (opcionPorDefecto) {
                        Text("Selecciona una opción")
                    }
                },
                modifier = Modifier
                    .clickable {
                        opcionPorDefecto = false
                        expandir = true
                    }
                    .fillMaxWidth()


            )

            if (!opcionPorDefecto) {
                DropdownMenu(
                    expanded = expandir,
                    onDismissRequest = {
                        expandir = false
                        if (rolSeleccionado.isEmpty()) {
                            opcionPorDefecto = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listaDeRoles.forEach { rol ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = rol
                                )
                            },
                            onClick = {
                                expandir =
                                    false
                                rolSeleccionado = rol
                            })
                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

//correo electronico
        Text(
            text = "Correo Electrónico",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = correoElectronico,
            onValueChange = {
                correoElectronico = it
            },
            placeholder = {
                Text("miCorreo@ejemplo.com")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

//repetir correo electronico
        Text(
            text = "Repetir correo Electrónico",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = correoElectronicoRepeticion,
            onValueChange = {
                correoElectronicoRepeticion = it
            },
            placeholder = {
                Text("miCorreo@ejemplo.com")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

//contraseña
        Text(
            text = "Contraseña",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = contrasenia,
            onValueChange = {
                contrasenia = it
            },
            placeholder = {
                Text("abc987!")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)

        )

        Spacer(modifier = Modifier.height(16.dp))

//repetir contraseña
        Text(
            text = "Repetir Contraseña",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(5.dp)
        )
        TextField(
            value = contraseniaRepeticion,
            onValueChange = {
                contraseniaRepeticion = it
            },
            placeholder = {
                Text("abc987!")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)

        )
        Spacer(modifier = Modifier.height(20.dp))

        var esVacioUnCampoDeRegistro by rememberSaveable { mutableStateOf(false) }
        var noCoincidenCorreosElectronicos by rememberSaveable { mutableStateOf(false) }
        var noCoincidenContrasenias by rememberSaveable { mutableStateOf(false) }

// Estado para controlar si se muestra el diálogo de éxito
        var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
// Estado para controlar si se muestra el diálogo de error
        var showErrorDialog by rememberSaveable { mutableStateOf(false) }
        var showEmailExistsErrorDialog by rememberSaveable { mutableStateOf(false) }

        Button(
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Registrarme")
        }


        Spacer(modifier = Modifier.height(20.dp))

    }

    //toma variable global, es por si aparece una nueva navegación y se han olvidado de agregarla
    if (!VariablesFuncionesGlobales.navegacionDefinida) {
        MensajeDialogoNohayNavegacionDefinida(
            showDialog = mostrarNoDefinidaNavegacion,
            onDismiss = { mostrarNoDefinidaNavegacion = false },
            onAccept = {
                //por ahora no hace nada
            }
        )

    }
}



