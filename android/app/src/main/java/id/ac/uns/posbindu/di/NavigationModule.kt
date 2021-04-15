package id.ac.uns.posbindu.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import id.ac.uns.posbindu.navigation.FragmentNavigator
import id.ac.uns.posbindu.navigation.Navigator

@Module
@InstallIn(ActivityComponent::class)
abstract class NavigationModule {
    @ActivityScoped
    @Binds
    abstract fun bindNavigator(fragmentNavigator: FragmentNavigator): Navigator
}