package br.com.elo.lio;

import android.app.Application;

import br.com.elo.lio.persistence.UserPersistence;

/**
 * LG ELECTRONICS INC.
 * LGEBR - SCA R&D, Sao Paulo, SP, Brazil
 * LGEMR - Santa Clara, CA, USA
 * Copyright(c) 2017 by LG Electronics Inc.
 * <p>
 * <p>
 * All rights reserved. No part of this work may be reproduced, stored in a retrieval system, or transmitted by any
 * means without prior written Permission of LG Electronics Inc.
 */
public class LIOApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        UserPersistence.newInstance(this);
    }
}
