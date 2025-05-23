package com.example.debttracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.debttracker.R
import com.example.debttracker.ui.screens.FriendDisplay
import com.example.debttracker.ui.theme.AccentPrimary
import com.example.debttracker.ui.theme.AppBackgroundColor
import com.example.debttracker.ui.theme.ComponentCornerRadiusBig
import com.example.debttracker.ui.theme.ComponentCornerRadiusSmall
import com.example.debttracker.ui.theme.TextPrimary
import com.example.debttracker.ui.theme.TilePrimary
import kotlin.math.abs

// a) TransactionField – kafelek z datą i kwotą transakcji
@Composable
fun TransactionField(date: String, amount: String, modifier: Modifier = Modifier) {
    Surface(
        color = TilePrimary,
        shape = RoundedCornerShape(ComponentCornerRadiusSmall),
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
            Text(text = date, color = TextPrimary)
            Text(text = amount, color = TextPrimary)
        }
    }
}

// b) FriendField – kafelek z danymi o znajomym
@Composable
fun FriendField(
    friend: FriendDisplay,
    navController: NavHostController,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    val painter = if (friend.imageRes != null)
        painterResource(id = friend.imageRes)
    else
        painterResource(id = R.drawable.placeholder)

    Surface(
        color = TilePrimary,
        shape = RoundedCornerShape(ComponentCornerRadiusSmall),
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
                    .clip(RoundedCornerShape(ComponentCornerRadiusSmall))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                CustomText(
                    text = friend.name,
                    fontSize = 16.sp
                )
                ColorBalanceText(balance = friend.balance, currencySymbol = currencySymbol)
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
        color = TilePrimary,
        shape = RoundedCornerShape(ComponentCornerRadiusSmall),
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
                    .clip(RoundedCornerShape(ComponentCornerRadiusSmall))
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
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                    Text(
                        text = username,
                        color = TextPrimary,
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
                            tint = TextPrimary,
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
                            tint = TextPrimary,
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
    fontSize: TextUnit = 24.sp,
    aspectRatio: Float = 5f,
    buttonWidth: Float = 0f,
    buttonShape: Shape = RoundedCornerShape(ComponentCornerRadiusBig),
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        !enabled -> Color.Gray
        variant == ButtonVariant.LIME -> AccentPrimary
        else -> Color.DarkGray
    }
    val textColor = when {
        !enabled -> Color.DarkGray
        variant == ButtonVariant.LIME -> Color.Black
        else -> Color.White
    }

    Surface(
        color = backgroundColor,
        shape = buttonShape,
        modifier = modifier
            .then(
                if (buttonWidth == 0f) Modifier.fillMaxWidth() else Modifier.width(buttonWidth.dp)
            )
            .aspectRatio(aspectRatio)
            .clickable(enabled = enabled) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = textColor,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            CustomText(
                text = text,
                fontSize = fontSize,
                color = textColor
            )
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
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    val textFieldBackground = TilePrimary
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextPrimary) },
        placeholder = {
            Text(
                text = placeholder,
                color = TextPrimary.copy(alpha = 0.7f)
            )
        },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
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
        shape = RoundedCornerShape(ComponentCornerRadiusSmall)
    )
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
    val dropdownBackground = TilePrimary

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .fillMaxWidth(),
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOption,
            onValueChange = {},
            label = { Text(label, color = TextPrimary) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(PrimaryNotEditable)
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
            ),
            shape = RoundedCornerShape(ComponentCornerRadiusSmall)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(dropdownBackground)
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, color = TextPrimary) },
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
fun BalanceField(label: String, balance: Float, currencySymbol: String = "$", modifier: Modifier = Modifier) {
    //val label = if (balance >= 0) "People owe you:" else "You owe people:"
    //val balanceColor = if (balance >= 0) Color.Green else Color.Red
    val balanceColor = TextPrimary
    Surface(
        color = TilePrimary,
        shape = RoundedCornerShape(ComponentCornerRadiusSmall),
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
            Text(text = label, color = TextPrimary)
            Text(text = "$currencySymbol${"%.2f".format(balance)}", color = balanceColor)
        }
    }
}

// h) CustomText - zwykły tekst z kolorem
@Composable
fun CustomText(
    text: String,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = TextPrimary,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier
    )
}

// Helper function to get currency symbol
fun getCurrencySymbol(currency: String): String {
    return when (currency) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "PLN" -> "PLN"
        "CZK" -> "Kč"
        else -> "$"
    }
}

// i) CustomNumberTextField - textfield with label and digit restrictions
@Composable
fun CustomNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    currency: String = "USD",
    modifier: Modifier = Modifier
) {
    val textFieldBackground = TilePrimary
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Allow empty string, digits, and up to 2 decimal places
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) {
                    onValueChange(newValue)
                }
            },
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextPrimary.copy(alpha = 0.7f)
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
            shape = RoundedCornerShape(ComponentCornerRadiusSmall),
            // Format with currency symbol based on selected currency
            prefix = {
                Text(
                    text = getCurrencySymbol(currency),
                    color = TextPrimary
                )
            }
        )
    }
}

// j) CustomUserAvatar - kafelek z awatarem użytkownika
@Composable
fun CustomUserAvatar(
    image: ImageBitmap? = null,
    imageRes: Int? = null,
    editable: Boolean = false,
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Image(
            painter = if (image != null) {
                BitmapPainter(image)
            } else if (imageRes != null) {
                painterResource(id = imageRes)
            } else {
                painterResource(id = R.drawable.profile_pic)
            },
            contentDescription = "User Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color.Red)
        )
        if (editable) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .background(color = Color.Blue, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Avatar",
                    tint = Color.Yellow,
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
    topBar: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
    sheetContent: @Composable () -> Unit
) {
    BottomSheetScaffold(
        topBar = topBar,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                sheetContent()
            }
        },
        sheetContainerColor = AccentPrimary,
        sheetPeekHeight = (LocalConfiguration.current.screenHeightDp * 0.4).dp,
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(5.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackgroundColor),
            contentAlignment = Alignment.TopCenter
        ) {
            content()
        }
    }
}


// l) ColorBalanceText - tekst z kolorem zależnym od wartości
@Composable
fun ColorBalanceText(balance: Float, currencySymbol: String = "$", fontSize: TextUnit = 32.sp) {
    val formattedBalance = String.format("%.2f", abs(balance))
    val balanceText = if (balance >= 0) "+$currencySymbol$formattedBalance" else "-$currencySymbol$formattedBalance"
    val balanceColor = if (balance >= 0f) Color.Green else Color.Red

    CustomText(
        text = balanceText,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        color = balanceColor
    )
}

