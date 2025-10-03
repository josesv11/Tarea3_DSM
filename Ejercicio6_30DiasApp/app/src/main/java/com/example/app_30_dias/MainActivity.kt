package com.example.app_30_dias
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ScienceFactsApp() }
    }
}


data class Insight(
    val day: Int,
    val title: String,
    val teaser: String,
    val body: String,
    @DrawableRes val imageRes: Int
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScienceFactsApp() {
    val blueNight = lightColorScheme(
        primary = Color(0xFF0D1B2A),
        onPrimary = Color(0xFFEAF1FF),
        secondary = Color(0xFF1B263B),
        surface = Color(0xFF0F1E2E),
        surfaceVariant = Color(0xFF1F2A44),
        onSurfaceVariant = Color(0xFFE0ECFF)
    )

    MaterialTheme(colorScheme = blueNight) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("30 días de curiosidades científicas") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = blueNight.primary,
                        titleContentColor = blueNight.onPrimary
                    )
                )
            }
        ) { padding ->
            HomeScreen(Modifier.padding(padding).padding(horizontal = 16.dp, vertical = 12.dp))
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val items = remember { loadInsights30() }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.day }) { item ->
            InsightTile(item)
        }
    }
}

@Composable
fun InsightTile(item: Insight) {
    var expanded by remember { mutableStateOf(false) }
    var pressed by remember { mutableStateOf(false) }
    val overlayAlpha by animateFloatAsState(if (pressed) 1f else 0f, label = "overlay")

    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(Modifier.fillMaxWidth()) {
            // Badge día
            AssistChip(
                onClick = {},
                label = { Text("Día ${item.day}") },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(start = 12.dp, top = 12.dp)
            )

            // Título
            Text(
                item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            // Imagen + overlay del “teaser” mientras se mantiene presionado
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { pressed = true },
                            onPress = {
                                pressed = true
                                try {
                                    tryAwaitRelease()
                                } finally {
                                    pressed = false
                                }
                            }
                        )
                    }
            ) {
                Image(
                    painter = painterResource(item.imageRes),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(overlayAlpha),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {}
                    Text(
                        text = item.teaser,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Resumen breve
            Text(
                item.teaser,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(12.dp)
            )

            // Botón de expansión
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.padding(horizontal = 8.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(if (expanded) "Ocultar explicación" else "Ver explicación")
            }

            // Bloque expandible
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(
                    text = item.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}


fun loadInsights30(): List<Insight> = listOf(
    Insight(
        1, "La miel casi no caduca",
        "Su baja humedad y acidez frenan microbios; por eso puede durar siglos.",
        "La miel es higroscópica (absorbe poca agua) y ligeramente ácida. Además, las abejas añaden glucosa oxidasa que genera pequeñas cantidades de peróxido de hidrógeno. Ese combo inhibe bacterias y hongos. Se han encontrado vasijas con miel en tumbas egipcias de miles de años aún comestible.",
        R.drawable.dia1
    ),
    Insight(
        2, "El agua tiene un ‘punto triple’",
        "Bajo condiciones precisas puede coexistir como hielo, líquido y vapor.",
        "En 0.01 °C y 611.657 Pa, el agua alcanza el punto triple: las tres fases están en equilibrio. Este punto es tan fundamental que se usa para calibrar termómetros de alta precisión y entender diagramas de fase en física y química.",
        R.drawable.dia2
    ),
    Insight(
        3, "Materia de estrella de neutrones",
        "Una cucharadita pesaría cerca de mil millones de toneladas en la Tierra.",
        "Cuando una estrella masiva colapsa, los protones y electrones se fusionan formando neutrones. El resultado es una esfera de ~10–12 km con densidad parecida a la del núcleo atómico. La gravedad es tan intensa que deforma el espacio-tiempo y acelera la rotación a cientos de vueltas por segundo.",
        R.drawable.dia3
    ),
    Insight(
        4, "Tu cuerpo emite luz",
        "Los humanos brillamos débilmente por reacciones metabólicas.",
        "La oxidación de lípidos y otras reacciones con radicales libres generan fotones (bioluminiscencia ultra débil). Es miles de veces más tenue que lo que el ojo humano puede captar, pero cámaras sensibles pueden registrarla, con picos que siguen el ritmo circadiano.",
        R.drawable.dia4
    ),
    Insight(
        5, "La ‘banana radiactiva’",
        "El potasio natural contiene un isótopo radiactivo inofensivo (K-40).",
        "El 0.012% del potasio es K-40. Una banana aporta una dosis extremadamente pequeña de radiación (la famosa ‘dosis banana’). Es útil para comparar riesgos: comer una banana no es peligroso; de hecho, el potasio es esencial para la vida.",
        R.drawable.dia5
    ),
    Insight(
        6, "Los días se alargan",
        "La fricción de mareas transfiere energía y frena la rotación terrestre.",
        "La Luna eleva mareas que rozan con los continentes y fondos oceánicos. Esa fricción disipa energía y empuja a la Luna lentamente hacia afuera. Resultado: el día terrestre se alarga ~1.7 ms por siglo; hace cientos de millones de años el día era notablemente más corto.",
        R.drawable.dia6
    ),
    Insight(
        7, "Eres un superorganismo",
        "Tu microbioma contiene tantos microbios como células humanas (o más).",
        "Bacterias, arqueas y hongos viven en tu intestino, piel y otras superficies. Ayudan a digerir, producir vitaminas y entrenar al sistema inmune. Cambios en la dieta, antibióticos o estrés pueden alterar su equilibrio con efectos en salud y ánimo.",
        R.drawable.dia7
    ),
    Insight(
        8, "El ‘wood-wide web’",
        "Hongos y raíces forman redes que comparten nutrientes e información.",
        "Las micorrizas conectan árboles de distintas especies. A través de estas redes se envían azúcares, nitrógeno y señales químicas que alertan de plagas. Algunos árboles ‘madre’ sostienen a plántulas sombreadas transfiriéndoles carbono.",
        R.drawable.dia8
    ),
    Insight(
        9, "¿El vidrio fluye?",
        "A temperatura ambiente es un sólido amorfo: no fluye perceptiblemente.",
        "Los vitrales viejos que parecen más gruesos abajo se deben a métodos antiguos de soplado y montaje. El vidrio moderno es rígido; su estructura desordenada lo hace ‘amorfo’, distinto a un cristal, pero no es un líquido en la práctica.",
        R.drawable.dia9
    ),
    Insight(
        10, "Lluvia de partículas cósmicas",
        "Rayos cósmicos chocan con la atmósfera y nos bañan continuamente.",
        "Protones y núcleos de alta energía generan cascadas de muones y otras partículas. Aunque la mayoría es detenida por la atmósfera, una fracción llega al suelo y atraviesa nuestros cuerpos sin que lo notemos.",
        R.drawable.dia10
    ),
    Insight(
        11, "ADN kilométrico en nanoespacio",
        "Cada célula alberga unos 2 metros de ADN empaquetados con precisión.",
        "Histonas, nucleosomas y bucles de cromatina compactan la molécula sin impedir que los genes se expresen cuando toca. El empaquetamiento es dinámico y cambia con señales químicas (epigenética).",
        R.drawable.dia11
    ),
    Insight(
        12, "El Sol es básicamente blanco",
        "La atmósfera lo hace parecer más amarillo o rojizo según la altura.",
        "La dispersión de Rayleigh desvía la luz azul; al amanecer y atardecer, el camino atmosférico es mayor y el rojo domina. En el espacio, el Sol se percibe blanco.",
        R.drawable.dia12
    ),
    Insight(
        13, "Materiales ‘más ligeros que el aire’",
        "Aerogeles de sílice parecen humo sólido y son excelentes aislantes.",
        "Están compuestos hasta en un 99% por aire en una red nanoporosa. Tienen bajísima conductividad térmica y se usan en aislamiento extremo, aunque son frágiles.",
        R.drawable.dia13
    ),
    Insight(
        14, "Metales traza en nosotros",
        "Tu cuerpo contiene oro, cobre y zinc en cantidades minúsculas.",
        "Estos metales participan indirectamente en enzimas y estructura de proteínas. El total de oro en un humano promedio es del orden de miligramos.",
        R.drawable.dia14
    ),
    Insight(
        15, "Baterías de frutas",
        "Una patata o limón pueden encender un LED como pila galvánica.",
        "Dos metales distintos (por ejemplo, cobre y zinc) y un electrolito ácido generan una diferencia de potencial. Es un gran experimento para entender reacciones redox y circuitos.",
        R.drawable.dia15
    ),
    Insight(
        16, "Arcoíris doble",
        "El secundario invierte los colores y es más tenue.",
        "Se produce por dos reflexiones internas de la luz en gotas de agua. Entre ambos arcos aparece la banda de Alejandro, una zona más oscura por menor iluminación.",
        R.drawable.dia16
    ),
    Insight(
        17, "El metal ‘se siente’ más frío",
        "No está más frío: solo conduce mejor el calor de tu mano.",
        "Los materiales con alta conductividad térmica extraen energía de tu piel más rápido, activando más receptores de frío. La temperatura real puede ser la misma que la de la madera cercana.",
        R.drawable.dia17
    ),
    Insight(
        18, "Cerebro hambriento",
        "Pesa ~2% del cuerpo pero consume ~20% de la energía en reposo.",
        "Las neuronas necesitan mantener gradientes iónicos y comunicarse constantemente. Por eso el cerebro es tan sensible a la falta de oxígeno y glucosa.",
        R.drawable.dia18
    ),
    Insight(
        19, "Relámpagos en erupciones",
        "Las nubes de ceniza pueden generar tormentas eléctricas propias.",
        "La colisión de partículas de ceniza separa cargas, que descargan como rayos espectaculares. Son útiles para detectar erupciones a distancia con sensores.",
        R.drawable.dia19
    ),
    Insight(
        20, "¿Lluvia de diamantes?",
        "Modelos sugieren que en planetas gigantes el carbono precipita como diamante.",
        "En Júpiter y Saturno, la presión y temperatura podrían convertir metano en carbono sólido, que ‘cae’ hasta capas más profundas antes de fundirse.",
        R.drawable.dia20
    ),
    Insight(
        21, "Estómago autorreparador",
        "La mucosa gástrica se renueva cada pocos días para soportar el ácido.",
        "Células especializadas secretan moco y bicarbonato formando una barrera que protege de la pepsina y el HCl. Por eso las úlceras se relacionan con desequilibrios en estas defensas.",
        R.drawable.dia21
    ),
    Insight(
        22, "El número e por todas partes",
        "Describe procesos de crecimiento continuo y decaimiento exponencial.",
        "Intereses compuestos, radiactividad y llegadas aleatorias (Poisson) usan (e) en sus fórmulas. Es la base de los logaritmos naturales y aparece en límites y series.",
        R.drawable.dia22
    ),
    Insight(
        23, "Cómo beben los gatos",
        "Aprovechan inercia y tensión superficial para levantar una columna de agua.",
        "La lengua toca la superficie y se retrae rápido; el chorro que sube queda atrapado por la velocidad antes de que la gravedad lo venza. Elegante y eficiente.",
        R.drawable.dia23
    ),
    Insight(
        24, "El color del camaleón",
        "No ‘pintan’ su piel: reconfiguran cristales fotónicos.",
        "Cambian el espaciamiento de nanocristales de guanina en la dermis, alterando qué longitudes de onda se reflejan. Es física de materiales, no tinta biológica.",
        R.drawable.dia24
    ),
    Insight(
        25, "Ardillas jardineras",
        "Al enterrar y olvidar semillas ayudan a regenerar bosques.",
        "Su comportamiento de ‘almacenamiento’ dispersa bellotas y otras semillas a distancia del árbol madre, aumentando las probabilidades de germinación.",
        R.drawable.dia25
    ),
    Insight(
        26, "Tu sombra tiene fuerza",
        "La luz ejerce presión; bloquear fotones reduce una fuerza minúscula.",
        "La presión de radiación del Sol es pequeñísima en la vida diaria, pero en el espacio permite propulsar velas solares sin combustible.",
        R.drawable.dia26
    ),
    Insight(
        27, "¿Oxígeno azul?",
        "El gas no se ve azul; el oxígeno líquido sí tiene un tono celeste.",
        "Los tanques pintados no indican su color real. El O₂ líquido muestra paramagnetismo fuerte y absorbe rojo, por eso luce azulado.",
        R.drawable.dia27
    ),
    Insight(
        28, "Relojes en la cima van más rápido",
        "La gravedad afecta el tiempo: relatividad general en acción.",
        "A mayor altitud, el potencial gravitatorio es más alto y el tiempo transcurre ligeramente más rápido. Satélites GPS corrigen este efecto y también la relatividad especial por su velocidad.",
        R.drawable.dia28
    ),
    Insight(
        29, "Pulpos con tres corazones",
        "Y sangre azul gracias a la hemocianina (cobre).",
        "Dos corazones bombean a las branquias y uno al resto del cuerpo. Son maestros del camuflaje y del aprendizaje, con un sistema nervioso distribuido en sus brazos.",
        R.drawable.dia29
    ),
    Insight(
        30, "Somos polvo de estrellas",
        "Los elementos de la vida se forjaron en estrellas y supernovas.",
        "El hidrógeno surgió tras el Big Bang, pero el carbono, oxígeno y hierro se cocinaron en hornos estelares. Explosiones y vientos estelares sembraron nubes que dieron origen a sistemas planetarios como el nuestro.",
        R.drawable.dia30
    ),
)