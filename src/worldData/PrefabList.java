package worldData;


import java.util.Arrays;
import java.util.Random;

/* FOR COPY PASTING
new Prefab(new int[][] {
	
},new int[][] {
	
}),
 */
//0 = Empty
//1 = Pedestal
//2 = Wall
//3 = Pillar
//4 = Fence

public class PrefabList {
	public static Prefab getRandomPrefab(int sizeCat) {
		Arrays.sort(prefabs);
		int i;
		
		for(i = prefabs.length-1; i >= 0; i--)
			if(prefabs[i].getSizeCategory() <= sizeCat) break;
		
		Random rnd = new Random();
		int j = rnd.nextInt(i+1);
		if (j > i) j = i;

		return prefabs[j];
	}
	
	
	public static Prefab[] prefabs = new Prefab[] {
	
	//SIZE CATEGORY 5 BLEOW HERE====================================
	
	//SIZE CATEGORY 4 BLEOW HERE====================================
	
	//SIZE CATEGORY 3 BLEOW HERE====================================
	new Prefab(new int[][] {
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,50,0,0,0,0,0,0,50,0,0,0,0,0},
		{0,0,0,0,0,50,0,0,0,0,0,0,50,0,0,0,0,0},
		{0,0,0,0,0,50,0,0,0,0,0,0,50,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,10,0,10,0,0,0,0,0,10,0,10,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,100,0,0,0,0,0,100,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
	},new int[][] {
		{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
		{2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
		{2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
		{2,0,0,0,4,0,0,0,0,0,0,0,0,4,0,0,0,2},
		{2,0,0,4,2,2,2,2,2,2,2,2,2,2,4,0,0,2},
		{2,0,0,0,2,0,0,1,0,0,1,0,0,2,0,0,0,2},
		{2,0,0,0,2,0,0,1,0,0,1,0,0,2,0,0,0,2},
		{2,0,0,0,2,0,0,1,0,0,1,0,0,2,0,0,0,2},
		{2,0,0,0,2,2,2,2,4,4,2,2,2,2,4,0,0,2},
		{2,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,2},
		{2,0,3,0,3,0,3,0,0,0,3,0,3,0,3,0,0,2},
		{2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
		{2,0,3,0,3,0,3,0,0,0,3,0,3,0,3,0,0,2},
		{2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
		{2,0,0,0,2,2,2,2,0,0,2,2,2,2,0,0,0,2},
		{2,0,0,0,0,0,0,2,0,0,2,0,0,0,0,0,0,2},
		{2,0,0,0,2,0,0,2,0,0,2,0,0,2,0,0,0,2},
		{2,2,2,2,2,2,2,2,0,0,2,2,2,2,2,2,2,2}
	}),
	//SIZE CATEGORY 2 BLEOW HERE====================================
	
	//decoration 1
	new Prefab(new int[][] {
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0}
	},new int[][] {
		{4,0,0,1,0,0,1,0,0,4},
		{0,4,0,0,0,0,0,0,4,0},
		{0,0,4,0,0,0,0,4,0,0},
		{1,0,0,4,0,0,4,0,0,1},
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0},
		{1,0,0,4,0,0,4,0,0,1},
		{0,0,4,0,0,0,0,4,0,0},
		{0,4,0,0,0,0,0,0,4,0},
		{4,0,0,1,0,0,1,0,0,4}
	}),
	
	new Prefab(new int[][] {
		{0,4,0,0,0,0,0,0,0,0},
		{0,4,0,4,4,4,4,4,4,0},
		{0,4,0,3,0,0,0,0,4,0},
		{0,4,0,3,0,1,2,0,4,0},
		{0,4,0,3,0,1,2,0,4,0},
		{0,4,0,3,0,0,2,0,4,0},
		{0,4,0,3,3,3,3,0,4,0},
		{0,4,0,0,0,0,0,0,4,0},
		{0,4,4,4,4,4,4,4,4,0},
		{0,0,0,0,0,0,0,0,0,0}
	},new int[][] {
		{4,0,4,4,4,4,4,4,4,4},
		{4,0,4,0,0,0,0,0,0,4},
		{4,0,4,0,4,4,4,4,0,4},
		{4,0,4,0,4,0,0,4,0,4},
		{4,0,4,0,4,0,0,4,0,4},
		{4,0,4,0,4,4,0,4,0,4},
		{4,0,4,0,0,0,0,4,0,4},
		{4,0,4,4,4,4,4,4,0,4},
		{4,0,0,0,0,0,0,0,0,4},
		{4,4,4,4,4,4,4,4,4,4}
	}),
	
	//bastion 1
	new Prefab(new int[][] {
		{0,0,0,0,0  ,0  ,0,0,0,0},
		{0,0,0,0,0  ,0  ,0,0,0,0},
		{0,0,0,0,0  ,0  ,0,0,0,0},
		{0,0,0,0,0  ,0  ,0,0,0,0},
		{0,0,0,0,100,100,0,0,0,0},
		{0,0,0,0,100,100,0,0,0,0},
		{0,0,0,0,0  ,0  ,0,0,0,0},
		{0,0,0,0,0  ,0  ,0,0,0,0},
		{0,0,0,0,0  ,0  ,0,0,0,0},
		{0,0,0,0,0  ,0  ,0,0,0,0}
	},new int[][] {
		{0,0,0,0,0,0,0,0,0,0},
		{0,0,4,4,4,4,4,4,0,0},
		{0,4,0,0,0,0,0,0,4,0},
		{0,4,0,0,2,2,0,0,4,0},
		{0,4,0,2,0,0,2,0,4,0},
		{0,4,0,2,0,0,2,0,4,0},
		{0,4,0,0,2,2,0,0,4,0},
		{0,4,0,0,0,0,0,0,4,0},
		{0,0,4,4,4,4,4,4,0,0},
		{0,0,0,0,0,0,0,0,0,0}
	}),
	//SIZE CATEGORY 1 BLEOW HERE====================================
	//tower 1
	new Prefab(new int[][] {
		{0 ,0 ,0 ,0 ,0 ,0 },
		{0 ,0 ,0 ,0 ,0 ,0 },
		{0 ,0 ,50,50,0 ,0 },
		{0 ,0 ,50,50,0 ,0 },
		{0 ,0 ,0 ,0 ,0, 0 },
		{0 ,0 ,0 ,0 ,0, 0 }
	}, new int[][] {
		{2,2,0,0,2,2},
		{2,0,0,0,0,2},
		{0,0,0,0,0,0},
		{0,0,0,0,0,0},
		{2,0,0,0,0,2},
		{2,2,0,0,2,2}
	}),
	//tower 2
	new Prefab(new int[][] {
		{0 ,0 ,0 ,0 ,0 ,0 },
		{0 ,0 ,0 ,0 ,0 ,0 },
		{0 ,0 ,50,50,0 ,0 },
		{0 ,0 ,50,50,0 ,0 },
		{0 ,0 ,0 ,0 ,0, 0 },
		{0 ,0 ,0 ,0 ,0, 0 }
	}, new int[][] {
		{0,0,2,2,0,1},
		{0,2,0,0,0,0},
		{2,0,0,0,0,2},
		{2,0,0,0,0,2},
		{0,0,0,0,2,0},
		{1,0,2,2,0,0}
	}),
	//tower 3
	new Prefab(new int[][] {
		{0 ,0 ,0 ,0 ,0 ,0 },
		{0 ,0 ,0 ,0 ,0 ,0 },
		{0 ,0 ,50,50,0 ,0 },
		{0 ,0 ,50,50,0 ,0 },
		{0 ,0 ,0 ,0 ,0, 0 },
		{0 ,0 ,0 ,0 ,0, 0 }
	}, new int[][] {
		{0,0,2,2,2,0},
		{0,0,0,3,2,2},
		{2,0,0,0,3,2},
		{2,3,0,0,0,2},
		{2,2,3,0,0,0},
		{0,2,2,2,0,0}
	}),
	//passage 1
	new Prefab(new int[][] {
		{0,0 ,0},
		{0,0 ,0},
		{0,10,0},
		{0,10,0},
		{0,0,0},
		{0,0 ,0}
	}, new int[][] {
		{2,0,2},
		{2,0,2},
		{2,0,2},
		{2,0,2},
		{2,0,2},
		{2,0,2},
	}),
	//SIZE CATEGORY 0 BLEOW HERE====================================
	new Prefab(new int[][] {
		{0,0,0},
		{0,30,0},
		{0,0,0}
	},new int[][] {
		{1,0,1},
		{0,0,0},
		{1,0,1}
	}),
	new Prefab(new int[][] {
		{0,0,0},
		{0,40,0},
		{0,0,0}
	},new int[][] {
		{1,0,1},
		{1,0,1},
		{1,0,1}
	}),
	new Prefab(new int[][] {
		{0,0,0},
		{0,30,0}

	},new int[][] {
		{1,1,1},
		{1,0,1}
	}),
	new Prefab(new int[][] {
		{0,0,0,0},
		{0,15,15,0},
		{0,15,15,0},
		{0,0,0,0}
	},new int[][] {
		{2,0,2,2},
		{2,0,0,2},
		{2,0,0,2},
		{2,2,0,2}
	}),
		
	};
}
