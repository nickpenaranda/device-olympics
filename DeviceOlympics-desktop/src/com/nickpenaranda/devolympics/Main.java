package com.nickpenaranda.devolympics;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.imagepacker.TexturePacker;
import com.badlogic.gdx.tools.imagepacker.TexturePacker.Settings;

public class Main {
	public static void main(String[] args) {
		// Repack on launch during dev
		Settings settings = new Settings();
		settings.padding = 2;
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.incremental = true;
		TexturePacker.process(settings, "../rawgfx/splash", "../DeviceOlympics-android/assets/gfx/splash");
		TexturePacker.process(settings, "../rawgfx/calibration", "../DeviceOlympics-android/assets/gfx/calibration");
		TexturePacker.process(settings, "../rawgfx/fitts", "../DeviceOlympics-android/assets/gfx/fitts");
		// end dev code
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "DeviceOlympics";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 800;
		
		new LwjglApplication(new DeviceOlympics(), cfg);
	}
}
