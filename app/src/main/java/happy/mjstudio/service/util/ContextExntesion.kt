package happy.mjstudio.service.util

import android.content.Context
import android.widget.Toast

fun Context.showSimpleToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()