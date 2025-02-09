package org.micks.champmaker.register;

import org.micks.champmaker.players.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class RegisterService {

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private RegisterPlayerRepository registerPlayerRepository;

    @Autowired
    private PlayerService playerService;

    public void registerTeamToChampionship(Long champId, RegisterDTO registerDTO) {
        RegisterEntity registerEntity = new RegisterEntity(champId, registerDTO.getTeamId(), registerDTO.getRegistrationDate());
        registerRepository.save(registerEntity);
    }

    public List<Long> getRegisteredPlayers(long champId, long teamId) {
        List<RegisterPlayerEntity> registeredPlayers = registerPlayerRepository.findByChampId(champId);
        List<Long> registeredPlayersIds = registeredPlayers.stream()
                .map(RegisterPlayerEntity::getPlayerId)
                .collect(toList());
        return playerService.getPlayersIds(teamId).stream()
                .filter(registeredPlayersIds::contains)
                .collect(toList());
    }

    public List<Long> getRegisteredTeams(long champId) {
        List<RegisterEntity> registerEntities = registerRepository.findByChampId(champId);
        return registerEntities.stream()
                .map(RegisterEntity::getTeamId).collect(toList());
    }

    public void registerPlayer(long champId, RegisterPlayerDTO registerPlayerDTO) {
        RegisterPlayerEntity registerPlayerEntity = new RegisterPlayerEntity(champId, registerPlayerDTO.getPlayerId());
        registerPlayerRepository.save(registerPlayerEntity);
    }
}
