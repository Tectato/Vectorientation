package vectorientation.main;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import vectorientation.main.config.SimpleConfig;

import java.util.HashSet;

public class Vectorientation implements ClientModInitializer {
	private static SimpleConfig CONFIG = SimpleConfig.of( "vectorientation" ).provider( Vectorientation::provider ).request();
	public static Identifier TNT_ID = Identifier.of("minecraft:tnt");

	public static String VAR_SQUETCH = "squetch";
	public static String VAR_MIN_WARP = "min_warp";
	public static String VAR_WARP_FACTOR = "warp_factor";
	public static String VAR_MINECARTS = "minecarts";
	public static String VAR_BLACKLIST = "blacklist";

	public static boolean SQUETCH = CONFIG.getOrDefault(VAR_SQUETCH, true);
	public static double MIN_WARP = CONFIG.getOrDefault(VAR_MIN_WARP, 0.75d);
	public static double WARP_FACTOR = CONFIG.getOrDefault(VAR_WARP_FACTOR, 1.0d);
	private static String BLACKLIST_STRING = CONFIG.getOrDefault(VAR_BLACKLIST, "anvil,chipped_anvil,damaged_anvil");
	public static HashSet<Identifier> BLACKLIST;
	public static boolean MINECARTS =  CONFIG.getOrDefault(VAR_MINECARTS, false);
	public static boolean TNT = true;
	
	@Override
	public void onInitializeClient() {
		if(CONFIG.isBroken()){
			System.out.println("[Vectorientation] Config found to be corrupted or outdated, resetting...");
			CONFIG.reconstructFile();
		}
		parseBlacklist();
		System.out.println("[Vectorientation] Initialized.");
	}

	private static void parseBlacklist(){
		BLACKLIST = new HashSet<>();
		String[] entries = BLACKLIST_STRING.replace(" ","").split(",");
		for(String entry : entries){
			Identifier identifier = Identifier.of(entry);
			if(Registries.BLOCK.containsId(identifier)){
				BLACKLIST.add(identifier);
			} else {
				System.out.println("[Vectorientation] Could not find block id \""+entry+"\"");
			}
		}
	}

	public static void setConfig(String key, String value){
		CONFIG.set(key, value);

		if(key.equals(VAR_MINECARTS)) MINECARTS = CONFIG.getOrDefault(key, false);
		if(key.equals(VAR_SQUETCH)) SQUETCH = CONFIG.getOrDefault(key, true);
		if(key.equals(VAR_MIN_WARP)) MIN_WARP = CONFIG.getOrDefault(key, 0.75d);
		if(key.equals(VAR_WARP_FACTOR)) WARP_FACTOR = CONFIG.getOrDefault(key, 1.0d);
		if(key.equals(VAR_BLACKLIST)){
			BLACKLIST_STRING = CONFIG.getOrDefault(VAR_BLACKLIST, "anvil,chipped_anvil,damaged_anvil");
			parseBlacklist();
		}
	}

	public static void writeConfig(){
		CONFIG.writeToFile();
	}
	
	private static String provider( String filename ) {
		return "# Enable Squash & Stretch:\n"
				+ "squetch=true\n\n"
				+ "# Vertical squish at 0 velocity:\n"
				+ "min_warp=0.75\n"
				+ "# Amount of squish increase with velocity:\n"
				+ "warp_factor=1.0\n\n"
				+ "# Whether Minecarts should be affected: (Quite janky)\n"
				+ "minecarts=false\n\n"
				+ "# List of blocks that should NOT be squished: (Comma separated)\n"
				+ "blacklist=anvil,chipped_anvil,damaged_anvil";
    }
}
