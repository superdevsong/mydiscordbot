package bots;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;


public class stoneBot extends ListenerAdapter {

	 public static void main(String[] args) throws Exception {
		    JDABuilder.create(args[0], GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
		        .addEventListeners(new stoneBot())
		        .build();
		  }

		  private final AudioPlayerManager playerManager;
		  private final Map<Long, GuildMusicManager> musicManagers;

		  private stoneBot() {
		    this.musicManagers = new HashMap<>();

		    this.playerManager = new DefaultAudioPlayerManager();
		    AudioSourceManagers.registerRemoteSources(playerManager);
		    AudioSourceManagers.registerLocalSource(playerManager);
		  }

		  private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
		    long guildId = Long.parseLong(guild.getId());
		    GuildMusicManager musicManager = musicManagers.get(guildId);

		    if (musicManager == null) {
		      musicManager = new GuildMusicManager(playerManager);
		      musicManagers.put(guildId, musicManager);
		    }

		    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

		    return musicManager;
		  }

		  @Override
		  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		    String[] command = event.getMessage().getContentRaw().split(" ", 2);
		    final TextChannel channel = event.getChannel();
		    VoiceChannel vchannel = event.getMember().getVoiceState().getChannel();
		  
		    if ("~시작".equals(command[0]) && command.length == 2) {
			      loadAndPlay(channel, command[1],vchannel);
			    } else if ("~다음".equals(command[0])) {
			      skipTrack(event.getChannel());
			    } else if ("~멈춰".equals(command[0])) {
				      stTrack(event.getChannel());
			    } else if ("~다시재생".equals(command[0])) {
				      reTrack(event.getChannel());
			    } else if ("~가라".equals(command[0])) {
				      deTrack(event.getChannel());
			    } else if("정연이".equals(command[0])) {
			    	channel.sendMessage("아름답다 ").queue();
			    } else if("이정연".equals(command[0])) {
			    	channel.sendMessage("사랑스럽다 ").queue();
			    } else if("~도움".equals(command[0])) {
			    	help(channel);
			    } else if("~뿡".equals(command[0])){
			    	gas(channel);
			    } else if("~놀아줘".equals(command[0])) {
			    	enjoy(channel);
			    } else if("~슬로투".equals(command[0])) {
			    	sloTwo(channel);
			    }
			    super.onGuildMessageReceived(event);
			  }
		  	  private void sloTwo(TextChannel channel) {
		  		  channel.sendMessage("https://cdn.discordapp.com/attachments/843106178862415895/863784219662221412/album_1faauddva.gif").queue();
		  		  
		  	  }
			  private void gas(TextChannel channel) {
				  channel.sendMessage("https://tenor.com/view/totoro-gah-scared-what-ahhh-gif-4880952").queue();
				  channel.sendMessage("으아아아악!").queue();
			  }
			  private void enjoy(TextChannel channel) {
				  channel.sendMessage("https://tenor.com/view/totoro-mei-friends-cute-bonding-gif-4073907").queue();
			  }
			  private void help(TextChannel channel) {
				  channel.sendMessage("도와드리죠^^\n ~시작 노래시작\n~다음 다음노래시작\n~멈춰 노래멈춤\n~다시재생 다시 재생함\n~가라 노래끝나고 나감\n정연이 이쁨\n이정연 사랑스러움\n~뿡\n~놀아줘 ").queue();
			  }

			  private void loadAndPlay(final TextChannel channel, final String trackUrl,final VoiceChannel vchannel) {
			    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

			    playerManager.loadItemOrdered(musicManager, "ytsearch:"+trackUrl, new AudioLoadResultHandler() {
			      @Override
			      public void trackLoaded(AudioTrack track) {
			        channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

			        play(channel.getGuild(), musicManager, track,vchannel);
			      }

			      @Override
			      public void playlistLoaded(AudioPlaylist playlist) {
			        AudioTrack firstTrack = playlist.getSelectedTrack();

			        if (firstTrack == null) {
			          firstTrack = playlist.getTracks().get(0);
			        }

			        channel.sendMessage("노래를 추가하였습니다. " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

			        play(channel.getGuild(), musicManager, firstTrack, vchannel);
			      }

			      @Override
			      public void noMatches() {
			        channel.sendMessage("Nothing found by " + trackUrl).queue();
			      }

			      @Override
			      public void loadFailed(FriendlyException exception) {
			        channel.sendMessage("Could not play: " + exception.getMessage()).queue();
			      }
			    });
			  }

			  private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, VoiceChannel vchannel) {
			    connectToFirstVoiceChannel(guild.getAudioManager(), vchannel);

			    musicManager.scheduler.queue(track);
			  }

			  private void skipTrack(TextChannel channel) {
			    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
			    musicManager.scheduler.nextTrack();

			    channel.sendMessage("다음곡을 틀게요 ^^").queue();
			  }
			  private void stTrack(TextChannel channel) {
				    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
				    musicManager.scheduler.stopTrack();

				    channel.sendMessage("노래를 멈출게요 ^^").queue();
				  }
			  private void reTrack(TextChannel channel) {
				    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
				    musicManager.scheduler.resumeTrack();

				    channel.sendMessage("다시 재생할게요 ^^").queue();
				  }
			  private void deTrack(TextChannel channel) {
				    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
				    musicManager.scheduler.deleteTrack();
				    connectToFirstVoiceChannel(channel.getGuild().getAudioManager(),null);
				    channel.sendMessage("그만 나가보겠습니다.").queue();
				  }

			  private static void connectToFirstVoiceChannel(AudioManager audioManager, VoiceChannel vchannel) {
				  if(vchannel==null||audioManager==null) {
				    	audioManager.closeAudioConnection();
				  }
				  else {
					  if (!audioManager.isConnected() || !audioManager.getConnectedChannel().equals(vchannel)) {
			    		audioManager.openAudioConnection(vchannel);
					  }
				  }
			 }
}
