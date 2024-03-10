package jbnu.se.api.service;

import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectoralRoll;
import jbnu.se.api.exception.ElectionNotFoundException;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import jbnu.se.api.request.ElectoralRollRequest;
import jbnu.se.api.util.ElectoralRollFileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectoralRollService {

    private final ElectoralRollRepository electoralRollRepository;

    private final ElectionRepository electionRepository;

    private final ElectoralRollFileHandler electoralRollFileHandler;

    public void registerElectoralRoll(ElectoralRollRequest electoralRollRequest) {
        Long electionId = electoralRollRequest.getElectionId();
        MultipartFile file = electoralRollRequest.getFile();

        Election election = electionRepository.findById(electionId)
                .orElseThrow(ElectionNotFoundException::new);

        List<ElectoralRoll> electoralRolls = electoralRollFileHandler.processFile(file);
        electoralRolls.forEach(electoralRoll -> electoralRoll.setElection(election));

        electoralRollRepository.saveAll(electoralRolls);
    }
}
