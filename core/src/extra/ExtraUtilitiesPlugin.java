package extra;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import static mindustry.Vars.state;


public class ExtraUtilitiesPlugin extends mindustry.mod.Plugin {
    public ExtraUtilitiesPlugin() {
        Log.info("|--> Extra-Utilities is loading...");
    }

    public void init(){
        Events.on(EventType.WorldLoadEvent.class, e -> {
            if(!state.serverPaused && Groups.player.size() == 0) {
                state.serverPaused = true;
                Log.info("[E-U] |--> The server was automatically paused at initialization");
            }
        });

        Events.on(EventType.PlayerJoin.class, e -> {
            int playersCount = Groups.player.size();
            Log.debug("[E-U] |--> (Join event) There are now " + playersCount + " players on the map");

            if(state.serverPaused && playersCount > 0) {
                state.serverPaused = false;
                Log.info("[E-U] |--> The server was automatically unpaused.");
                Call.sendMessage("[#bebebe]Server was [red]unpaused.");
            }
        });

        Events.on(EventType.PlayerLeave.class, e -> {
            // The group size is the number of players before one disconnects from the
            // server.
            int playersCount = Groups.player.size() - 1;
            Log.debug("[E-U] |--> (Leave event) There are now " + playersCount + " players on the map");

            if(!state.serverPaused && playersCount == 0) {
                state.serverPaused = true;
                Log.info("[E-U] |--> The server was automatically paused.");
            }
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("pause", "<on/off>", "Pause/Unpause the game.", (arg, player) -> {
            if(!player.admin) {
                player.sendMessage("[scarlet]You don't have the required permission to execute this command.");
                return;
            }

            if("on".equals(arg[0])) {
                if (state.serverPaused) {
                    player.sendMessage("[scarlet]Server is already paused.");
                    return;
                }

                state.serverPaused = true;
                Call.sendMessage("[#bebebe]Server [green]paused [#bebebe]by [#ffffff] " + player.name + ".");
            } else if("off".equals(arg[0])) {
                if (!state.serverPaused) {
                    player.sendMessage("[scarlet]Server is already unpaused.");
                    return;
                }

                state.serverPaused = false;
                Call.sendMessage("[#bebebe]Server [red]unpaused [#bebebe]by [#ffffff] " + player.name + ".");
            } else {
                player.sendMessage("[scarlet]Need argument 'on' or 'off'.");
            }
        });
    }
}