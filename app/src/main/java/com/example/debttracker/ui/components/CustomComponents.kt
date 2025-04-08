package com.example.debttracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.debttracker.R
import com.example.debttracker.ui.theme.BottomNavBarColor
import com.example.debttracker.ui.theme.GlobalTopBarColor

// a) TransactionField – kafelek z datą i kwotą transakcji
@Composable
fun TransactionField(date: String, amount: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color.DarkGray,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = date, color = Color.White)
            Text(text = amount, color = Color.White)
        }
    }
}

// b) FriendField – kafelek z danymi o znajomym
@Composable
fun FriendField(
    friendName: String,
    balance: Float,
    imageRes: Int? = null, // jak null to bedzie placeholder.jpeg
    modifier: Modifier = Modifier
) {
    val painter = if (imageRes != null)
        painterResource(id = imageRes)
    else
        painterResource(id = R.drawable.placeholder)

    Surface(
        color = Color.DarkGray,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painter,
                contentDescription = "Friend image",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(text = friendName, color = Color.White)
                val balanceText = "Balance: $balance"
                val balanceColor = if (balance >= 0f) Color.Green else Color.Red
                Text(text = balanceText, color = balanceColor)
            }
        }
    }
}

// c) FriendInvitationField – kafelek z zaproszeniem do znajomych
@Composable
fun FriendInvitationField(
    friendName: String,
    username: String,
    imageRes: Int? = null,
    modifier: Modifier = Modifier,
    onAccept: () -> Unit = {},
    onReject: () -> Unit = {}
) {
    val painter = if (imageRes != null)
        painterResource(id = imageRes)
    else
        painterResource(id = R.drawable.placeholder)

    Surface(
        color = Color.DarkGray,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painter,
                contentDescription = "Invitation image",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = friendName, color = Color.White)
                Text(text = username, color = Color.White)
                Row {
                    IconButton(onClick = onAccept) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Accept",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onReject) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Reject",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

// d) CustomButton – (na razie zmienia kolor po wciśnięciu, kiedyś to naprawię żeby był uniwersalny)
@Composable
fun CustomButton(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    var clicked by remember { mutableStateOf(false) }
    val backgroundColor = if (clicked) Color.Green else Color.DarkGray

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(5f)
            .clickable { clicked = !clicked }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = Color.White)
        }
    }
}

// e) CustomTextField – textfield z etykietą
@Composable
fun CustomTextField(label: String, text: String, onTextChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val textFieldBackground = BottomNavBarColor
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = textFieldBackground,
                unfocusedContainerColor = textFieldBackground,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                disabledTextColor = Color.Gray,
                disabledLabelColor = Color.Gray,
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

// f) CustomEnumPickField – pole wyboru z rozwijanym menu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomEnumPickField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val dropdownBackground = GlobalTopBarColor

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOption,
            onValueChange = {},
            label = { Text(label, color = Color.White) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = dropdownBackground,
                unfocusedContainerColor = dropdownBackground,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                disabledTextColor = Color.Gray,
                disabledLabelColor = Color.Gray,
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(dropdownBackground)
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, color = Color.White) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

// g) BalanceField – pole wyświetlające bilans (tekst po lewej i kwotę po prawej, kolor zależny od wartości)
@Composable
fun BalanceField(balance: Float, modifier: Modifier = Modifier) {
    val label = if (balance >= 0) "People owe you:" else "You owe people:"
    val balanceColor = if (balance >= 0) Color.Green else Color.Red
    Surface(
        color = Color.DarkGray,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = Color.White)
            Text(text = balance.toString(), color = balanceColor)
        }
    }
}
