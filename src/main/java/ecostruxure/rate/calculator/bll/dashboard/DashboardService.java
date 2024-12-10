package ecostruxure.rate.calculator.bll.dashboard;

import ecostruxure.rate.calculator.dal.IProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final IProjectRepository projectRepository;

    public DashboardService(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


}
