package squeek.applecore;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

import net.minecraftforge.common.config.Configuration;
import squeek.applecore.mixinplugin.TargetedMod;

public class ModConfig
{
	public static Configuration config;
	
	public static final String LANG_PREFIX = "applecore.cfg.";

	/*
	 * SERVER
	 */
	public static final String CATEGORY_SERVER = "server";
	private static final String CATEGORY_SERVER_COMMENT =
		"These config settings are server-side only";

	public static double EXHAUSTION_SYNC_THRESHOLD = ModConfig.EXHAUSTION_SYNC_THRESHOLD_DEFAULT;
	public static double EXHAUSTION_SYNC_THRESHOLD_DEFAULT = 0.01D;
	private static final String EXHAUSTION_SYNC_THRESHOLD_NAME = "exhaustion.sync.threshold";
	private static final String EXHAUSTION_SYNC_THRESHOLD_COMMENT =
		"The maximum difference between the server's value for exhaustion and the client's before the value is syncronized from the server to the client.\n"
			+ "Raising this value will cause fewer packets to be sent, but will make the client's exhaustion values appear more choppy";

	/*
	 * CLIENT
	 */
	public static final String CATEGORY_CLIENT = "client";
	private static final String CATEGORY_CLIENT_COMMENT =
			"These config settings are client-side only";

	public static boolean SHOW_FOOD_VALUES_IN_TOOLTIP = true;
	private static final String SHOW_FOOD_VALUES_IN_TOOLTIP_NAME = "show.food.values.in.tooltip";
	private static final String SHOW_FOOD_VALUES_IN_TOOLTIP_COMMENT =
			"If true, shows the hunger and saturation values of food in its tooltip while holding SHIFT";

	public static boolean ALWAYS_SHOW_FOOD_VALUES_TOOLTIP = false;
	private static final String ALWAYS_SHOW_FOOD_VALUES_TOOLTIP_NAME = "show.food.values.in.tooltip.always";
	private static final String ALWAYS_SHOW_FOOD_VALUES_TOOLTIP_COMMENT =
			"If true, shows the hunger and saturation values of food in its tooltip automatically (without needing to hold SHIFT)";

	public static boolean SHOW_SATURATION_OVERLAY = true;
	private static final String SHOW_SATURATION_OVERLAY_NAME = "show.saturation.hud.overlay";
	private static final String SHOW_SATURATION_OVERLAY_COMMENT =
			"If true, shows your current saturation level overlayed on the hunger bar";

	public static boolean SHOW_FOOD_VALUES_OVERLAY = true;
	private static final String SHOW_FOOD_VALUES_OVERLAY_NAME = "show.food.values.hud.overlay";
	private static final String SHOW_FOOD_VALUES_OVERLAY_COMMENT =
			"If true, shows the hunger (and saturation if " + SHOW_SATURATION_OVERLAY_NAME + " is true) that would be restored by food you are currently holding";

	@Deprecated
	private static final String SHOW_FOOD_EXHAUSTION_OVERLAY_NAME = "show.food.exhaustion.hud.overlay";

	public static boolean SHOW_FOOD_EXHAUSTION_UNDERLAY = true;
	private static final String SHOW_FOOD_EXHAUSTION_UNDERLAY_NAME = "show.food.exhaustion.hud.underlay";
	private static final String SHOW_FOOD_EXHAUSTION_UNDERLAY_COMMENT =
			"If true, shows your food exhaustion as a progress bar behind the hunger bars";

	public static boolean SHOW_FOOD_DEBUG_INFO = true;
	private static final String SHOW_FOOD_DEBUG_INFO_NAME = "show.food.stats.in.debug.overlay";
	private static final String SHOW_FOOD_DEBUG_INFO_COMMENT =
			"If true, adds a line that shows your hunger, saturation, and exhaustion level in the F3 debug overlay";
	
	/*
	 * GENERAL
	 */
	public static final String CATEGORY_GENERAL = Configuration.CATEGORY_GENERAL;
    public static final String CATEGORY_GENERAL_COMMENT = "These config settings are for both server and client-side";
	
	public static String[] REQUIRED_MODS;
	public static final String[] REQUIRED_MODS_DEFAULTS = Arrays.stream(TargetedMod.values()).map(mod -> mod.modName).toArray(String[]::new);
	public static final String REQUIRED_MODS_NAME = "required.mods";
	public static final String REQUIRED_MODS_COMMENT = "Subset of TargetMods that are required";
	public static final Pattern REQUIRED_MODS_VALIDATION_PATTERN = Pattern.compile(String.join("|", Arrays.stream(TargetedMod.values()).map(mod -> "^" + mod.modName + "$").toArray(String[]::new)));

	public static void init(File file)
	{
		config = new Configuration(file);

		load();
		sync();
	}

	public static void sync()
	{
		/*
		 * SERVER
		 */
		config.getCategory(CATEGORY_SERVER).setLanguageKey(LANG_PREFIX + CATEGORY_SERVER).setComment(CATEGORY_SERVER_COMMENT);

		EXHAUSTION_SYNC_THRESHOLD = config.get(CATEGORY_SERVER, EXHAUSTION_SYNC_THRESHOLD_NAME, EXHAUSTION_SYNC_THRESHOLD_DEFAULT, EXHAUSTION_SYNC_THRESHOLD_COMMENT).setLanguageKey(LANG_PREFIX + EXHAUSTION_SYNC_THRESHOLD_NAME).getDouble();

		/*
		 * CLIENT
		 */
		config.getCategory(CATEGORY_CLIENT).setLanguageKey(LANG_PREFIX + CATEGORY_CLIENT).setComment(CATEGORY_CLIENT_COMMENT);

		// rename overlay to underlay
		boolean foodExhaustionOverlayValue = config.get(CATEGORY_CLIENT, SHOW_FOOD_EXHAUSTION_OVERLAY_NAME, true).getBoolean();
		config.getCategory(CATEGORY_CLIENT).remove(SHOW_FOOD_EXHAUSTION_OVERLAY_NAME);

		SHOW_FOOD_VALUES_IN_TOOLTIP = config.get(CATEGORY_CLIENT, SHOW_FOOD_VALUES_IN_TOOLTIP_NAME, true, SHOW_FOOD_VALUES_IN_TOOLTIP_COMMENT).setLanguageKey(LANG_PREFIX + SHOW_FOOD_VALUES_IN_TOOLTIP_NAME).getBoolean();
		ALWAYS_SHOW_FOOD_VALUES_TOOLTIP = config.get(CATEGORY_CLIENT, ALWAYS_SHOW_FOOD_VALUES_TOOLTIP_NAME, false, ALWAYS_SHOW_FOOD_VALUES_TOOLTIP_COMMENT).setLanguageKey(LANG_PREFIX + ALWAYS_SHOW_FOOD_VALUES_TOOLTIP_NAME).getBoolean();
		SHOW_SATURATION_OVERLAY = config.get(CATEGORY_CLIENT, SHOW_SATURATION_OVERLAY_NAME, true, SHOW_SATURATION_OVERLAY_COMMENT).setLanguageKey(LANG_PREFIX + SHOW_SATURATION_OVERLAY_NAME).getBoolean();
		SHOW_FOOD_VALUES_OVERLAY = config.get(CATEGORY_CLIENT, SHOW_FOOD_VALUES_OVERLAY_NAME, true, SHOW_FOOD_VALUES_OVERLAY_COMMENT).setLanguageKey(LANG_PREFIX + SHOW_FOOD_VALUES_OVERLAY_NAME).getBoolean();
		SHOW_FOOD_EXHAUSTION_UNDERLAY = config.get(CATEGORY_CLIENT, SHOW_FOOD_EXHAUSTION_UNDERLAY_NAME, foodExhaustionOverlayValue, SHOW_FOOD_EXHAUSTION_UNDERLAY_COMMENT).setLanguageKey(LANG_PREFIX + SHOW_FOOD_EXHAUSTION_UNDERLAY_NAME).getBoolean();
		SHOW_FOOD_DEBUG_INFO = config.get(CATEGORY_CLIENT, SHOW_FOOD_DEBUG_INFO_NAME, true, SHOW_FOOD_DEBUG_INFO_COMMENT).setLanguageKey(LANG_PREFIX + SHOW_FOOD_DEBUG_INFO_NAME).getBoolean();
		
		/*
		 * GENERAL
		 */
        config.getCategory(CATEGORY_GENERAL).setLanguageKey(LANG_PREFIX + CATEGORY_GENERAL).setComment(CATEGORY_GENERAL_COMMENT);
        
		REQUIRED_MODS = config.get(CATEGORY_GENERAL, REQUIRED_MODS_NAME, REQUIRED_MODS_DEFAULTS, REQUIRED_MODS_COMMENT, false, TargetedMod.values().length, REQUIRED_MODS_VALIDATION_PATTERN).setLanguageKey(LANG_PREFIX + REQUIRED_MODS_NAME).setRequiresMcRestart(true).getStringList();

		if (config.hasChanged())
			save();
	}

	public static void save()
	{
		config.save();
	}

	public static void load()
	{
		config.load();
	}
}
