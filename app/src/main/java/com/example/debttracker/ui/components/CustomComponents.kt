package com.example.debttracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.NavHostController
import com.example.debttracker.R
import com.example.debttracker.ui.screens.User
import com.example.debttracker.ui.theme.BottomNavBarColor
import com.example.debttracker.ui.theme.ComponentCornerRadius
import com.example.debttracker.ui.theme.GlobalTopBarColor
import com.example.debttracker.ui.theme.LimeGreen
import kotlin.math.abs

// a) TransactionField – kafelek z datą i kwotą transakcji
@Composable
fun TransactionField(date: String, amount: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color.DarkGray,
        shape = RoundedCornerShape(ComponentCornerRadius),
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
    friend: User,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val painter = if (friend.imageRes != null)
        painterResource(id = friend.imageRes)
    else
        painterResource(id = R.drawable.placeholder)

    val formattedBalance = String.format("%.2f", abs(friend.balance))
    val balanceText = if (friend.balance >= 0) "+$$formattedBalance" else "-$$formattedBalance"
    val balanceColor = if (friend.balance >= 0f) Color.Green else Color.Red

    Surface(
        color = Color.DarkGray,
        shape = RoundedCornerShape(ComponentCornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3.5f)
            .clickable {
                navController.navigate("friend_info/${friend.id}")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = "Friend image",
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(ComponentCornerRadius))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = friend.name,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = balanceText,
                    color = balanceColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
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
        shape = RoundedCornerShape(ComponentCornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2.25f)
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
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(ComponentCornerRadius))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Column {
                    Text(
                        text = friendName,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Text(
                        text = username,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    IconButton(
                        onClick = onAccept,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Accept",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    IconButton(
                        onClick = onReject,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Reject",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
    }
}

// d) CustomButton – (na razie zmienia kolor po wciśnięciu, kiedyś to naprawię żeby był uniwersalny)
enum class ButtonVariant {
    GREY, LIME
}
@Composable
fun CustomButton(
    variant: ButtonVariant = ButtonVariant.GREY,
    icon: ImageVector? = null,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (variant) {
        ButtonVariant.LIME -> LimeGreen
        ButtonVariant.GREY -> Color.DarkGray
    }
    val textColor = when (variant) {
        ButtonVariant.LIME -> Color.White
        ButtonVariant.GREY -> Color.LightGray
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(ComponentCornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(5f)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = text, tint = textColor)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, color = textColor)
        }
    }
}


// e) CustomTextField – textfield z etykietą
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val textFieldBackground = BottomNavBarColor
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color.White.copy(alpha = 0.7f)) },
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
                disabledLabelColor = Color.Gray
            ),
            shape = RoundedCornerShape(ComponentCornerRadius)
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
fun BalanceField(label: String, balance: Float, modifier: Modifier = Modifier) {
    //val label = if (balance >= 0) "People owe you:" else "You owe people:"
    //val balanceColor = if (balance >= 0) Color.Green else Color.Red
    val balanceColor = Color.White
    Surface(
        color = Color.DarkGray,
        shape = RoundedCornerShape(ComponentCornerRadius),
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

// h) CustomText - zwykły tekst z kolorem
@Composable
fun CustomText(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize,
        color = Color.White,
        modifier = modifier
    )
}

// i) CustomNumberTextField - textfield z etykietą i ograniczeniem do cyfr
@Composable
fun CustomNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val textFieldBackground = BottomNavBarColor
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) {
                    onValueChange(newValue)
                }
            },
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = textFieldBackground,
                unfocusedContainerColor = textFieldBackground,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                disabledTextColor = Color.Gray,
                disabledLabelColor = Color.Gray
            ),
            shape = RoundedCornerShape(ComponentCornerRadius)
        )
    }
}

// j) CustomUserAvatar - kafelek z awatarem użytkownika
@Composable
fun CustomUserAvatar(
    image: ImageBitmap?,
    editable: Boolean,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Image(
            painter = if (image != null) {
                BitmapPainter(image)
            } else {
                painterResource(id = R.drawable.profile_pic)
            },
            contentDescription = "User Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color.Gray)
        )
        if (editable) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .background(color = Color.White, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Avatar",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// k) CustomBottomSheetScaffold - własny bottom sheet z kolorem i kształtem
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheetScaffold(
    sheetContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    BottomSheetScaffold (
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3000.dp),
                contentAlignment = Alignment.Center
            ) {
                sheetContent()
            }
        },
        sheetContainerColor = LimeGreen,
        sheetPeekHeight = (LocalConfiguration.current.screenHeightDp * 0.4).dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            content()
        }
    }
}
