package com.histsys;

import com.blade.Blade;
import com.histsys.config.Env;
import io.github.biezhi.anima.Anima;
import io.github.biezhi.anima.dialect.PostgreSQLDialect;

public class Starter {
    public void start() {
        // start database
        initDatabase();
        // start mvc
        Blade.of().start();
    }

    public static void main(String[] args) {
        new Starter().start();
    }

    private void initDatabase() {
        String dbUrl = Env.get("HISTSYS_DB_URL", "jdbc:postgresql://localhost:5433/csys");
        String dbUser = Env.get("HISTSYS_DB_USERNAME", "neo");
        String dbPass = Env.get("HISTSYS_DB_PASSWORD", "");
        Anima anima = Anima.open(dbUrl, dbUser, dbPass);
        anima.dialect(new PostgreSQLDialect()); // 分页代理
    }
}
