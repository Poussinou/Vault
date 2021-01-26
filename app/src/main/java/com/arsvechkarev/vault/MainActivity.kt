package com.arsvechkarev.vault

import android.os.Bundle
import com.arsvechkarev.core.navigation.Options
import com.arsvechkarev.vault.core.AndroidThreader
import com.arsvechkarev.vault.core.BaseActivity
import com.arsvechkarev.vault.core.Singletons.masterPasswordChecker
import com.arsvechkarev.vault.core.Singletons.masterPasswordSaver
import com.arsvechkarev.vault.core.Singletons.userAuthSaver
import com.arsvechkarev.vault.core.navigation.Navigator
import com.arsvechkarev.vault.core.navigation.NavigatorView
import com.arsvechkarev.vault.features.info.InfoScreen
import com.arsvechkarev.vault.features.list.presentation.PasswordsListScreen
import com.arsvechkarev.vault.features.password.PasswordCheckingDialog
import com.arsvechkarev.vault.features.start.StartScreen
import com.arsvechkarev.vault.password.MasterPasswordHolder
import com.arsvechkarev.vault.viewbuilding.Colors
import com.arsvechkarev.vault.viewdsl.Densities
import com.arsvechkarev.vault.viewdsl.Size.Companion.MatchParent
import com.arsvechkarev.vault.viewdsl.addView
import com.arsvechkarev.vault.viewdsl.classNameTag
import com.arsvechkarev.vault.viewdsl.size
import com.arsvechkarev.vault.viewdsl.withViewBuilder

class MainActivity : BaseActivity(), Navigator {
  
  private val navigator get() = viewAs<NavigatorView>()
  
  private val mainActivityLayout
    get() = withViewBuilder {
      RootFrameLayout {
        addView {
          NavigatorView(context).apply {
            classNameTag()
            size(MatchParent, MatchParent)
          }
        }
        addView {
          PasswordCheckingDialog(context, AndroidThreader, masterPasswordChecker).apply {
            classNameTag()
          }
        }
      }
    }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Densities.init(resources)
    Colors.init(this)
    setContentView(mainActivityLayout)
    if (userAuthSaver.isUserAuthorized()) {
      if (masterPasswordSaver.getSavedMasterPassword() != null) {
        navigator.navigate(PasswordsListScreen::class)
      } else {
        MasterPasswordHolder.setMasterPassword("qwerty//123")
        navigator.navigate(PasswordsListScreen::class)
        //        viewAs<PasswordCheckingDialog>().initiatePasswordCheck(onSuccess = { password ->
        //          MasterPasswordHolder.setMasterPassword(password)
        //          navigator.navigate(PasswordsListScreen::class)
        //        })
      }
    } else {
      navigator.navigate(StartScreen::class)
    }
  }
  
  override fun onBackPressed() {
    if (!navigator.handleGoBack()) {
      super.onBackPressed()
    }
  }
  
  override fun goToPasswordsListScreen() {
    navigator.navigate(PasswordsListScreen::class)
  }
  
  override fun goToNewPasswordScreen(serviceName: String) {
    navigator.navigate(InfoScreen::class, options = Options(
      arguments = Bundle().apply {
        putString(InfoScreen.KEY_SERVICE_NAME, serviceName)
      }))
  }
  
  override fun popCurrentScreen() {
    navigator.handleGoBack()
  }
}