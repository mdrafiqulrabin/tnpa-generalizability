import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

df_main = pd.read_csv('../data/num_of_stmt.csv')
y_tricks_map = {
    'pcp5': '$\leq 5$',
    'pcp10': '$6 \sim 10$',
    'pcp15': '$11 \sim 15$',
    'pcp20': '$16 \sim 20$',
    'pcp25': '$21 \sim 25$',
    'pcp50': '$26 \sim 50$',
    'pcp100': '$51 \sim 100$',
    'pcp1k': '$\geq 101$'
}


def draw_for_db(model, db):
    df = df_main.loc[(df_main['model'] == model_) & (df_main['dataset'] == db)]

    legends = df['transformation'].to_list()
    df = df.drop(['model', 'dataset', 'transformation', 'pcp'], axis=1)
    df = df.transpose()
    df.columns = legends

    df.plot.line(rot=25)

    plt.yticks(np.arange(0, 101, 10))
    ax = plt.subplot(111)
    ax.legend(loc='upper center', prop={'size': 13}, bbox_to_anchor=(0.5, 1.23), ncol=3, fancybox=True, shadow=True)

    labels = [y_tricks_map.get(item.get_text(), item.get_text()) for item in ax.get_xticklabels()]
    ax.set_xticklabels(labels)
    ax.set_xlim(left=0, right=len(y_tricks_map)-1)
    ax.set_ylim(bottom=0)

    plt.xlabel('Number of Statements', size=16, labelpad=10)
    plt.ylabel('Change of Prediction (%)', size=16)

    plt.rc('legend', fontsize=12)
    plt.tick_params(labelsize=14)

    plt.gcf().subplots_adjust(bottom=0.25, top=0.8)
    plt.savefig('{}_{}_num_of_stmt.png'.format(model, db), dpi=400)


for model_ in ['code2vec', 'code2seq', 'ggnn']:
    for db_ in ['JS', 'JM', 'JL']:
        print(f'Plotting for {model_} - {db_}...')
        draw_for_db(model_, db_)
