package com.auresus.academy.di.koin

import com.auresus.academy.view.splash.SplashActivity
import org.koin.core.qualifier.named
import org.koin.dsl.module


/**
 *  Created by Sahil Bharti on 5/4/19.
 *
 *  Copyright (c) 2019 Sahil Inc. All rights reserved.
*/

var splashActivityModule = module {

    scope(named<SplashActivity>()) {
        scoped {
         //todo define provide object here  which is used for splashActivty Scope only
        }
    }
}






