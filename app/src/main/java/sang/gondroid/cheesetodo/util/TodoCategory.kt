package sang.gondroid.cheesetodo.util

import androidx.annotation.StringRes
import sang.gondroid.cheesetodo.R

enum class TodoCategory(
    @StringRes val categoryNameId: Int
) {
    ALL(R.string.all),
    ANDROID(R.string.android),
    LANGUAGE(R.string.language),
    DB(R.string.db),
    OTHER(R.string.other);
}