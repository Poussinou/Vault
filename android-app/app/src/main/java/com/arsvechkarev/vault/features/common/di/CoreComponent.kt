package com.arsvechkarev.vault.features.common.di

import android.app.Application
import com.arsvechkarev.vault.features.common.di.modules.BiometricsModule
import com.arsvechkarev.vault.features.common.di.modules.BiometricsModuleImpl
import com.arsvechkarev.vault.features.common.di.modules.CoreModule
import com.arsvechkarev.vault.features.common.di.modules.CoreModuleImpl
import com.arsvechkarev.vault.features.common.di.modules.DomainModule
import com.arsvechkarev.vault.features.common.di.modules.DomainModuleImpl
import com.arsvechkarev.vault.features.common.di.modules.ImagesLoadingModule
import com.arsvechkarev.vault.features.common.di.modules.ImagesLoadingModuleImpl
import com.arsvechkarev.vault.features.common.di.modules.IoModule
import com.arsvechkarev.vault.features.common.di.modules.IoModuleImpl
import com.arsvechkarev.vault.features.common.di.modules.KeePassModule
import com.arsvechkarev.vault.features.common.di.modules.KeePassModuleImpl
import com.arsvechkarev.vault.features.common.di.modules.NavigationModule
import com.arsvechkarev.vault.features.common.di.modules.NavigationModuleImpl
import com.arsvechkarev.vault.features.common.di.modules.ObserversModule
import com.arsvechkarev.vault.features.common.di.modules.ObserversModuleImpl
import com.arsvechkarev.vault.features.common.di.modules.PasswordsModule
import com.arsvechkarev.vault.features.common.di.modules.PasswordsModuleImpl
import com.arsvechkarev.vault.features.common.di.modules.PreferencesModule
import com.arsvechkarev.vault.features.common.di.modules.PreferencesModuleImpl

interface CoreComponent :
  CoreModule,
  PreferencesModule,
  IoModule,
  PasswordsModule,
  NavigationModule,
  ObserversModule,
  ImagesLoadingModule,
  BiometricsModule,
  DomainModule,
  KeePassModule {
  
  companion object {
    
    fun create(
      application: Application,
      extraDependenciesFactory: ExtraDependenciesFactory,
    ): CoreComponent {
      val coreModule = CoreModuleImpl(application)
      val extraDependencies = extraDependenciesFactory.getExtraDependencies()
      val preferencesModule = PreferencesModuleImpl(coreModule)
      val ioModule = IoModuleImpl(
        coreModule,
        extraDependencies.okHttpClient,
        extraDependencies.externalFileReader,
        extraDependencies.passwordsFileExporter,
        extraDependencies.uriPersistedMaker
      )
      val domainModule = DomainModuleImpl(
        coreModule,
        preferencesModule,
        extraDependencies.backupInterceptor
      )
      val keePassModule = KeePassModuleImpl()
      val imagesLoadingModule = ImagesLoadingModuleImpl(
        coreModule,
        ioModule,
        preferencesModule,
        extraDependencies.imageRequestsRecorder
      )
      return CoreComponentImpl(
        coreModule = coreModule,
        preferencesModule = preferencesModule,
        ioModule = ioModule,
        passwordsModule = PasswordsModuleImpl(domainModule),
        navigationModule = NavigationModuleImpl(extraDependencies.activityResultWrapper),
        observersModule = ObserversModuleImpl(),
        imagesLoadingModule = imagesLoadingModule,
        biometricsModule = BiometricsModuleImpl(coreModule, preferencesModule),
        domainModule = domainModule,
        keePassModule = keePassModule,
      )
    }
  }
}

class CoreComponentImpl(
  private val coreModule: CoreModule,
  private val preferencesModule: PreferencesModule,
  private val ioModule: IoModule,
  private val passwordsModule: PasswordsModule,
  private val navigationModule: NavigationModule,
  private val observersModule: ObserversModule,
  private val imagesLoadingModule: ImagesLoadingModule,
  private val biometricsModule: BiometricsModule,
  private val domainModule: DomainModule,
  private val keePassModule: KeePassModule,
) : CoreComponent,
  CoreModule by coreModule,
  PreferencesModule by preferencesModule,
  IoModule by ioModule,
  PasswordsModule by passwordsModule,
  NavigationModule by navigationModule,
  ObserversModule by observersModule,
  ImagesLoadingModule by imagesLoadingModule,
  BiometricsModule by biometricsModule,
  DomainModule by domainModule,
  KeePassModule by keePassModule
