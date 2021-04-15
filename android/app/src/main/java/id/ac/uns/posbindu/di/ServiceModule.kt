package id.ac.uns.posbindu.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import id.ac.uns.posbindu.auth.AuthService
import id.ac.uns.posbindu.auth.FirebaseAuthService
import id.ac.uns.posbindu.config.ConfigService
import id.ac.uns.posbindu.config.FirebaseConfigService
import id.ac.uns.posbindu.form.FormService
import id.ac.uns.posbindu.form.StubFormService
import id.ac.uns.posbindu.profile.FirebaseProfileRepository
import id.ac.uns.posbindu.profile.ProfileRepository

@Module
@InstallIn(ActivityComponent::class)
abstract class ServiceModule {
    @ActivityScoped
    @Binds
    abstract fun bindAuthService(authService: FirebaseAuthService): AuthService

    @ActivityScoped
    @Binds
    abstract fun bindConfigService(configService: FirebaseConfigService): ConfigService

    @ActivityScoped
    @Binds
    abstract fun bindFormService(formService: StubFormService): FormService

    @ActivityScoped
    @Binds
    abstract fun bindProfileService(profileService: FirebaseProfileRepository): ProfileRepository
}