package br.com.elo.lio;

import android.app.Application;

import br.com.elo.lio.persistence.HistoryPersistence;
import br.com.elo.lio.persistence.UserPersistence;

public class LIOApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        UserPersistence.newInstance(this);
        HistoryPersistence.newInstance(this);
    }
}
