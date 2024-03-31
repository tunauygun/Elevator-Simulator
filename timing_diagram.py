"""
SYSC3303 - Winter 2024 Timinig diagram plot

Python script to plot timing diagram for multiple tasks using matplotlib and numpy

Requirements:
# Install matplotlib
        pip install matplotlib
        pip3 install matplotlib

# Install numpy
        pip install numpy
        pip3 install numpy

Last edited: March 21st, 2024

Copy right: Dr. Rami Sabouni
"""
import matplotlib.pyplot as plt
import numpy as np


def scheduling_plot(tasks_states: list[list[int]], num_states=3) -> None:
    """ Plot the timinig diagram of tasks_states

        Preconditions:
            - The task states should be 0 (stopped), 1 (idle), or 2 (running)
            - 0 < len(tasks_states) <= 4

        >>> scheduling_plot([[2, 2, 2, 2],[1, 1, 1, 1],[1, 1, 1, 1]], 3)
        Plots the timinig diagram of these three tasks

        >>> scheduling_plot([[2, 2, 2, 2],[1, 1, 1, 1]])
        Plots the timinig diagram of these two tasks

    """
    for i in range(1, len(tasks_states)):
        # increment task2_values by 4 to fit in the second part of the plot
        tasks_states[i] = [(x + 4 * i) for x in tasks_states[i]]

    time_points = range(len(tasks_states[1]))

    num_tasks = len(tasks_states)

    y_ticks = range(num_tasks * (num_states + 1))
    y_tick_labels = ['stopped', 'idle', 'running', ''] * num_tasks

    # Plotting the timing diagram
    for i in range(num_tasks):
        plt.step(time_points, tasks_states[i], where='post', linewidth=2)
        plt.plot(time_points, [3 + (4*i)]*len(time_points), color='black')

        # Count clusters of consecutive Running
        clusters = count_running_time(tasks_states[i], 2 + (4 * i))

        # Generate x and y values for step plot
        x_values = np.arange(len(tasks_states[i]))
        y_values = tasks_states[i]

        # Plotting the step plot
        plt.step(x_values, y_values, where='post',
                 label='Original List Values')

        # Annotate cluster sizes at the beginning of each cluster
        for start_idx, cluster_size in clusters:
            # Calculate center index of the cluster
            center_idx = start_idx + cluster_size // 2
            plt.annotate(f'{cluster_size}', (center_idx, tasks_states[i][center_idx]), textcoords="offset points", xytext=(
                0, -15), ha='center', fontsize=12)

    plt.xlabel('Time')
    if num_tasks == 1:
        plt.ylabel('Elevator 0')
    elif num_tasks == 2:
        plt.ylabel('Elevator 0                          ' + 'Elevator 1')
    elif num_tasks == 3:
        plt.ylabel('Elevator 0                          ' +
                   'Elevator 1                          ' + 'Elevator 2')
    elif num_tasks == 4:
        plt.ylabel('Elevator 3                          ' + 'Elevator 0                          ' +
                   'Elevator 1                          ' + 'Elevator 2                          ')

    # Start at 0, end at len(tasks_states[0])+1, display every 10th tick
    plt.xticks(np.arange(0, len(tasks_states[0])+1, 10))
    plt.minorticks_on()

    plt.xlim(0, len(tasks_states[0]))  # Limit x-axis from 0 to 100
    plt.yticks(y_ticks, y_tick_labels)
    plt.title('Timing Diagram')
    # plt.grid(True)
    plt.grid(axis='both', which='major', color='gray',
             linestyle='--', linewidth=0.5)
    plt.show()


def count_running_time(task_states: list[tuple[int, int]], running_value: int):
    """ Returns a list of number of occurences of running states for a taks and
        when did it start running

        >>> count_running_time([0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 1, 2, 2], 2)
            [(1, 3), (5, 5), (11, 2)]

        >>> count_running_time([1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2], 2)
            [(2, 4), (8, 5)]

    """
    clusters = []
    count = 0
    cluster_start = None

    for idx, num in enumerate(task_states):
        if num == running_value:
            count += 1
            if cluster_start is None:
                cluster_start = idx
        elif count > 0:
            clusters.append((cluster_start, count))
            count = 0
            cluster_start = None

    if count > 0:
        clusters.append((cluster_start, count))

    return clusters


if __name__ == "__main__":

    example1_tasks = [[1,2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
                      [1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2,2, 2, 2, 2, 2, 2, 2, 2, 2,
                          2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0,0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
                      [1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0 , 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2,2,2,2,2,2,2,2,2,2,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1]]
    scheduling_plot(example1_tasks)

