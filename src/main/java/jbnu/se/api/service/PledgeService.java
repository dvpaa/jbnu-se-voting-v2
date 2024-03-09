package jbnu.se.api.service;

import jakarta.transaction.Transactional;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.domain.Pledge;
import jbnu.se.api.exception.GCSException;
import jbnu.se.api.exception.HeadquarterNotFoundException;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.PledgeRepository;
import jbnu.se.api.request.PledgeRequest;
import jbnu.se.api.util.GoogleCloudStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PledgeService {

    private final PledgeRepository pledgeRepository;

    private final HeadquarterRepository headquarterRepository;

    private final GoogleCloudStorageUtil googleCloudStorageUtil;

    @Transactional
    public void createPledge(PledgeRequest pledgeRequest) {
        Headquarter headquarter = headquarterRepository.findById(pledgeRequest.getHeadquarterId())
                .orElseThrow(HeadquarterNotFoundException::new);

        Pledge pledge = new Pledge();
        pledge.setDescription(pledgeRequest.getDescription());

        if (pledgeRequest.getImageFile() != null) {
            String imagePath;
            try {
                imagePath = googleCloudStorageUtil.uploadFile(pledgeRequest.getImageFile());
            } catch (IOException e) {
                throw new GCSException(e);
            }
            pledge.setPosterPath(imagePath);
        }

        Pledge savedPledge = pledgeRepository.save(pledge);
        headquarter.setPledge(savedPledge);
    }
}
