package org.micks.champmaker.players;

import org.micks.champmaker.EntityNotFoundException;
import org.micks.champmaker.teams.TeamEntity;
import org.micks.champmaker.teams.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TeamRepository teamRepository;

    public PlayerDTO getPlayer(long playerId) throws EntityNotFoundException {
        Optional<PlayerEntity> optionalPlayer = playerRepository.findById(playerId);
        if (optionalPlayer.isPresent()) {
            PlayerEntity playerEntity = optionalPlayer.get();
            return new PlayerDTO(playerEntity.getPlayerName(), playerEntity.getPlayerNumber(), playerEntity.getPlayerYear(), playerEntity.getTeam().getId());
        } else {
            throw new EntityNotFoundException("Cannot find player with Id: " + playerId);
        }
    }

    public void createPlayer(PlayerDTO playerDTO) throws EntityNotFoundException {
        Optional<TeamEntity> team = teamRepository.findById(playerDTO.getTeamId());
        if (team.isEmpty()) {
            throw new EntityNotFoundException("Cannot find team by Id: " + playerDTO.getTeamId());
        }
        PlayerEntity playerEntity = new PlayerEntity(playerDTO.getPlayerName(), playerDTO.getPlayerNumber(), playerDTO.getPlayerYear(), team.get());
        playerRepository.save(playerEntity);
    }

    public List<PlayerDTO> getPlayers(GetPlayersRequest getPlayersRequest) {
        List<PlayerEntity> playerList = playerRepository.findAll();
        return playerList.stream()
                .filter(playerEntity -> getPlayersRequest.getTeamId() == null || playerEntity.getTeam().getId().equals(getPlayersRequest.getTeamId()))
                .filter(playerEntity -> getPlayersRequest.getName() == null
                        || playerEntity.getPlayerName().toLowerCase().contains(getPlayersRequest.getName().toLowerCase()))
                .map(playerEntity -> new PlayerDTO(playerEntity.getPlayerName(), playerEntity.getPlayerNumber(), playerEntity.getPlayerYear(), playerEntity.getTeam().getId()))
                .collect(Collectors.toList());
    }

    public List<Long> getPlayersIds(long teamId) {
        List<PlayerEntity> allPlayers = playerRepository.findAll();
        List<PlayerEntity> playersForTeam = allPlayers.stream()
                .filter(playerEntity -> playerEntity.getTeam().getId() == teamId)
                .collect(Collectors.toList());
        return playersForTeam.stream()
                .map(PlayerEntity::getId)
                .collect(Collectors.toList());
    }
}
