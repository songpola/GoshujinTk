package tk.goshujin.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

class MainFragmentFactory : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            HomeFragment::class.java.name -> HomeFragment.newInstance()
            else -> super.instantiate(classLoader, className)
        }
    }
}