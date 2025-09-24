from locust import LoadTestShape
from config.config import cfg, logger


class CustomLoadShape(LoadTestShape):
    """
        Здесь должны быть описаны типы нагрузки с помощью stages
    """
    match cfg.loadshape_type:
        case 'baseline':
            stages = [
                {'duration': 50, 'users': 2, 'spawn_rate': 1}
            ]
        case 'fixeload':
            stages = [
                {'duration': 300, 'users': 10, 'spawn_rate': 1}
            ]
        case 'stages':
            stages = [
                {'duration': 600, 'users': 10, 'spawn_rate': 2},
                {'duration': 600, 'users': 20, 'spawn_rate': 2},
                {'duration': 600, 'users': 30, 'spawn_rate': 2},
                {'duration': 600, 'users': 40, 'spawn_rate': 2},
                {'duration': 600, 'users': 50, 'spawn_rate': 2},
            ]

    def tick(self):
        run_time = self.get_run_time()
        cumulative_time = 0  # Накопленное время всех предыдущих ступеней

        for stage in self.stages:
            cumulative_time += stage["duration"]
            if run_time < cumulative_time:  # Сравниваем с накопленным временем
                return (stage["users"], stage["spawn_rate"])

        return None



    # def tick(self):  # стандартная функция локаста, взятая из документации, для работы с кастомными "Лоад-Шейпами"
    #     run_time = self.get_run_time()
    #
    #     for stage in self.stages:
    #         if run_time < stage["duration"]:
    #             tick_data = (stage["users"], stage["spawn_rate"])
    #             return tick_data
    #
    #     return None
