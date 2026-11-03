package panaderiarosario.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import panaderiarosario.model.EstadoPedido

@Composable
fun ScreenTitle(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
        subtitle?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
fun ErrorText(message: String?) {
    if (!message.isNullOrBlank()) Surface(color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(message, color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(14.dp))
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    TextButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text("Volver")
    }
}

@Composable
fun InfoCard(content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { content() }
    }
}

/** Local vector illustration, available offline at every display density. */
@Composable
fun BreadMark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(84.dp)) {
        val w = size.width; val h = size.height
        drawOval(Color(0xFFE5D9C6), topLeft = Offset(w * .08f, h * .76f), size = Size(w * .84f, h * .13f))
        rotate(-24f) {
            drawRoundRect(Color(0xFFB76E32), Offset(w * .09f, h * .26f), Size(w * .82f, h * .48f), CornerRadius(h * .24f))
            drawRoundRect(Color(0xFFE7B36E), Offset(w * .09f, h * .22f), Size(w * .82f, h * .46f), CornerRadius(h * .23f))
            for (x in listOf(.32f, .50f, .68f)) {
                drawLine(Color(0xFFFFE6B8), Offset(w * x, h * .32f), Offset(w * (x - .06f), h * .54f), strokeWidth = w * .045f)
            }
        }
    }
}

@Composable
fun EmptyState(title: String, description: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        BreadMark()
        Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text(description, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun StatusChip(estado: EstadoPedido) {
    val label = when (estado) {
        EstadoPedido.RECIBIDO -> "Recibido"
        EstadoPedido.EN_PREPARACION -> "En preparación"
        EstadoPedido.EN_CAMINO -> "En camino"
        EstadoPedido.ENTREGADO -> "Entregado"
    }
    val background = if (estado == EstadoPedido.ENTREGADO) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer
    Surface(color = background, shape = CircleShape) {
        Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}
