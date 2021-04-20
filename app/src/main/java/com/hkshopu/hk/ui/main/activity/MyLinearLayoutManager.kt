
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager


class MyLinearLayoutManager : LinearLayoutManager {
    private var isScrollEnabled = true

    constructor(context: Context?, isScrollEnabled: Boolean) : super(context) {
        this.isScrollEnabled = isScrollEnabled
    }

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context,
        orientation,
        reverseLayout) {
    }

    override fun canScrollVertically(): Boolean {
        //設定是否禁止滑動
        return isScrollEnabled && super.canScrollVertically()
    }

    companion object {
        private val TAG = MyLinearLayoutManager::class.java.simpleName
    }
}