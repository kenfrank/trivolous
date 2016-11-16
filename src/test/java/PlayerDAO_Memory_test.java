

import java.util.ArrayList;
import java.util.List;

import com.trivolous.game.dao.PlayerDao;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;

public class PlayerDAO_Memory_test implements PlayerDao {
	
	static ArrayList<Player> players = new ArrayList<Player> ();
	static int id = 0;
	@Override
	public void create(Player player)
	{
		// set associations
		player.getGame().getPlayers().add(player);
		player.getMember().getPlayers().add(player);
		
		player.setId(id++);
		players.add(player);
	}

	@Override
	public void remove(Player player)
	{
		players.remove(player);
	}

	@Override
	public Player findById(long id)
	{
		for (Player p : players)
		{
			if (p.getId() == id) return p;
		}
		return null;
	}

	@Override
	public List<Player> findByMember(Member member) {
		// TODO Auto-generated method stub
		return null;
	}

}
