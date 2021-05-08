package com.arsvechkarev.vault.features.creating_password

import com.arsvechkarev.vault.core.model.PasswordCharacteristics
import com.arsvechkarev.vault.cryptography.PasswordStrength
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface PasswordCreatingView : MvpView {
  
  fun showCreatingPasswordMode()
  
  fun showEditingPasswordMode(password: String)
  
  fun showChangePasswordLength(progress: Int)
  
  fun showPasswordStrength(strength: PasswordStrength?)
  
  fun showPasswordCharacteristics(characteristics: Collection<PasswordCharacteristics>)
  
  fun showGeneratedPassword(password: String)
  
  fun showPasswordIsEmpty()
  
  fun showPasswordAcceptingDialog()
  
  fun hidePasswordAcceptingDialog()
  
  fun showLoadingDialog()
  
  fun hideLoadingDialog()
}