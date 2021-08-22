package sang.gondroid.cheesetodo.util

import androidx.annotation.StringRes
import sang.gondroid.cheesetodo.R

enum class UserRank(
    @StringRes val userRankStringId : Int
) {
    Level1(R.string.level1),
    Level2(R.string.level2),
    Level3(R.string.level3),
    Level4(R.string.level4),
    Level5(R.string.level5),
    Level6(R.string.level6),
    Level7(R.string.level7),
    Level8(R.string.level8),
    Level9(R.string.level9),
    Level10(R.string.level10),
    Level11(R.string.level11),
    Level12(R.string.level12);
}