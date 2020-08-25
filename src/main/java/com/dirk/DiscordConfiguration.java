/*
 * MIT License
 *
 * Copyright (c) 2020 Wesley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dirk;

import com.dirk.helper.Log;
import com.dirk.helper.RegisterListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.listener.GloballyAttachableListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DiscordConfiguration {
    private final ApplicationContext applicationContext;
    private final String discordApiKey;
    private final String discordStatus;
    private DiscordApi discordApi;


    @Autowired
    public DiscordConfiguration(ApplicationContext applicationContext, @Value("${discord.api.key}") String discordApiKey, @Value("${discord.status}") String discordStatus) {
        this.applicationContext = applicationContext;
        this.discordApiKey = discordApiKey;
        this.discordStatus = discordStatus;
    }

    @Bean
    public void startDiscordBot() {
        this.discordApi = new DiscordApiBuilder()
                .setToken(discordApiKey)
                .login()
                .join();

        initialize();
    }

    @Bean
    public DiscordApi getDiscordApi() {
        return this.discordApi;
    }

    public void initialize() {
        Map<String, RegisterListener> listeners = applicationContext.getBeansOfType(RegisterListener.class);

        // Register all listeners with RegisterListener marker interface
        for (RegisterListener listener : listeners.values()) {
            discordApi.addListener((GloballyAttachableListener) listener);
            Log.info("Registered listener " + listener.getClass().getName());
        }

        discordApi.updateActivity(discordStatus);

        Log.info("Successfully initialized");
        Log.info("Discord invite link: " + discordApi.createBotInvite());
    }
}
