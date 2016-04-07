#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<time.h>
#include<math.h>

/* num cities */
int nc = -1;
/* num tracks */
int nt = -1;
/* num missions */
int nm = -1;
/* mistakes */
int const MIS = 24;
int mistakes[MIS] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
char* mistaketext[MIS] = {"keword Limits missing","keyword Staedte missing","keyword Strecken missing","keyword Auftraege missing","Keywords without ':'","colour limit missing","MaxTracks Limit missing","negative limits","no cities","no tracks","no missions","invalid colour limit", "invalid MaxTracks limit","invalid city(no name)","invalid city(name duplicate)","invalid city(coordinates duplicate)","invalid track(to few fields)","invalid track(same origin and destination)","invalid track(invalid cost(<1||>6))","invalid mission(mission duplicate)","invalid mission(to few fields)","invalid mission(invalid city id)","invalid track(invalid city id)","comment without '#'"};
char* colours[9] = {"RED","GREEN","BLACK","YELLOW","BLUE","VIOLET","WHITE","ORANGE","ALL"};

void printhelp(){
	printf("\nuse: ./mapfile [--help] [-C <num cities>] [-T <num track>] [-Q <num missions>] [-M <mistakes>]. \nnumber-codes for mistakes are:\n");
	for (int i=0;i<10;i++)
		printf("%d:  %s\n",i,mistaketext[i]);
	for (int i=10;i<MIS;i++)
		printf("%d: %s\n",i,mistaketext[i]);
	printf("all numbers must be positive, or they will be ignored\nany input can be overwritten by later inputs\nexample: ./mapfile -C 20 -T 13 -M 1 -M 5 -M 15 \n\n");
}

/* changes ASCII-Numbers into Integer. Does not support negative numbers, or floating points!!!*/
int atoint(char* s){
	int sum = 0;
	for (int i = 0; i<strlen(s); i++){
		sum *= 10;
		sum += s[i] - 48;
	}
	return sum;
}

int main(int argc, char** argv){
	
	/* read inputs 
	* 
	* num cities
	* num tracks
	* num missions
	* num mistakes
	*/
	
	/* read inputs  */
	if (argc > 1){
		if (!strcmp(argv[1],"--help")){
			printhelp();
			return 0;
		}
		for (int i=1; i<(argc-1); i+=2){
			if (!strcmp(argv[i],"-C")){
				/* Cities */
				nc = atoint(argv[i+1]);
			} else {
				if (!strcmp(argv[i],"-T")){
					/* Tracks */
					nt = atoint(argv[i+1]);
				} else {
					if (!strcmp(argv[i],"-Q")){
						/* Missions (Quests) */
						nm = atoint(argv[i+1]);
					} else {
						if (!strcmp(argv[i],"-M")){
							/* Mistakes */
							if (atoint(argv[i+1])<0 || atoint(argv[i+1])>=MIS)
								return 2;
							mistakes[atoint(argv[i+1])] = 1;
						} else {
							/* invalid argument */
							printf("invalid argument!\n");
							printhelp();
							return 1;
						}
					}
				}
			}
		}
	}
	/* change negative values in the numbers to random */
	srand(time(NULL));
	if (nc<=0) 				/* number cities */
		nc = (rand()%500) + 5;
	if (nt<nc) 				/* number tracks */
		nt = nc * 3 + (rand()%100)-50;
	if (nm<=0) 				/* number missions */
		nm = nc/2+(rand()%44)-22;
	int tl = nt+(rand()%10); 		/* Tracklimit */
	int rc = (nc+80)/10+(rand()%3);		/* Ressourcecards  */
	
	printf("\nstarting map creation with: \n%d cities \n%d tracks \n%d missions \n%d tracklimit \n%d ressource cards per colour\n",nc, nt, nm, tl, rc);
	 
	for (int i=0;i<10;i++)
		if (mistakes[i])
			printf("%d:  %s\n",i,mistaketext[i]);
	for (int i=10;i<MIS;i++)
		if (mistakes[i])
			printf("%d: %s\n",i,mistaketext[i]);
	
	/* open the file */
	char filename[10];
	filename[9] = 0;
	strcpy(filename,"map-");
	filename[4] = (nc%10)+48;
	filename[5] = (nt%10)+48;
	filename[6] = (nm%10)+48;
	filename[7] = (tl%10)+48;
	filename[8] = (rc%10)+48;

	FILE* testfile = fopen(filename, "r");
	while (testfile != NULL){
		fclose(testfile);
		filename[4] = (rand()%10)+48;
		filename[5] = (rand()%10)+48;
		filename[6] = (rand()%10)+48;
		filename[7] = (rand()%10)+48;
		filename[8] = (rand()%10)+48;
		FILE* testfile = fopen(filename, "r");
	}
	fclose(testfile);	
	FILE* mapfile = fopen(filename, "w");
	printf("filename: %s\n",filename);
	
	/* write the file
	* mistakes will be highligted with comments
	*/
	
	/* LIMITS: */
	if (!mistakes[0]){
		if (!mistakes[4]){
			fputs("Limits:\n",mapfile);
		} else { 
			fputs("Limits\n",mapfile);
		}
	}
	/* colours and MaxTracks: */
	fprintf(mapfile,"RED,%d\n",rc);
	if (!mistakes[11])
		fprintf(mapfile,"GREEN,%d\n",rc);
	else 
		fprintf(mapfile,"GREEN%d\n",rc);
	if (!mistakes[5])
		fprintf(mapfile,"BLACK,%d\n",rc);
	if (!mistakes[7])
		fprintf(mapfile,"YELLOW,%d\n",rc);
	else 
		fprintf(mapfile,"YELLOW,%d\n",-(4+rc*2));
	fprintf(mapfile,"BLUE,%d\n",rc);
	if (!mistakes[11])
		fprintf(mapfile,"VIOLET,%d\n",rc);
	else 
		fprintf(mapfile,"VIOLET, %d\n",rc);
	if (!mistakes[12]&&!mistakes[6]){
		switch(rand()%4){
			case 0:	fprintf(mapfile,"WHITE,%d\nORANGE,%d\nALL,%d\nMaxTracks,%d\n",rc,rc,(int)(rc*1.2),tl); break;
			case 1:	fprintf(mapfile,"ALL,%d\nORANGE,%d\nWHITE,%d\nMaxTracks,%d\n",(int)(rc*1.2),rc,rc,tl); break;
			case 2:	fprintf(mapfile,"ORANGE,%d\nMaxTracks,%d\nALL,%d\nWHITE,%d\n",rc,tl,(int)(rc*1.2),rc); break;
			case 3:	fprintf(mapfile,"WHITE,%d\nALL,%d\nORANGE,%d\nMaxTracks,%d\n",rc-1,rc*2,(int)(rc*1.2),tl); break;
			default: break;
		}
	} else {
		fprintf(mapfile,"WHITE,%d\nORANGE,%d\nALL,%d\n",rc,rc,rc+4);
		if (mistakes[12])
			fprintf(mapfile,"MaxTracks,0\n");
	}
	/* cities: */
	if (!mistakes[1])
		fprintf(mapfile,"Staedte:\n");
	/* if necessary, open city file */
	int cityfile = nc<2060;
	if (!cityfile){
		printf("as of now, only up to 2059 cities are supported. unlimited support coming soon!\n");
		return 3;
	}
	FILE* cities = fopen("Cities","r");
	
	int side = (int)sqrt(nc*17);
	
	if (!mistakes[8]){
		for (int n=0;n<nc;n++){
			char* buffer = malloc(128);
			fgets(buffer, 128, cities);
			int p = 0;
			while (buffer[p]!='\n'){
				fputc(buffer[p],mapfile);
				p++;
			}
			fprintf(mapfile,",%d,%d\n",(n*17)%side,(n*17)/side);
		}
		if (mistakes[13])
			fprintf(mapfile,",%d,%d\n",((nc)*17)%side,((nc)*17)/side);
		if (mistakes[14])
			fprintf(mapfile,"Duplikat,%d,%d\nDuplikat,%d,%d\n",((nc+1)*17)%side,((nc+1)*17)/side,((nc+2)*17)%side,((nc+2)*17)/side);
		if (mistakes[15])
			fprintf(mapfile,"Zweimal,%d,%d\nKoordinaten,%d,%d\n",((nc+3)*17)%side,((nc+3)*17)/side,((nc+3)*17)%side,((nc+3)*17)/side);
	}
	/* tracks: */
	if (!mistakes[2])
		fprintf(mapfile,"Strecken:\n");	
	if (!mistakes[9]){
		for (int i=0;i<(nc-1);i++){
			fprintf(mapfile,"%d,%d,%d,%s,%c\n",i,i+1,(rand()%5)+1,colours[rand()%9],'N');
		}
		for (int i=nc;i<nt;i++){
			fprintf(mapfile,"%d,%d,%d,%s,%c\n",i%(nc/2),i%(nc/2)+(rand()%(nc/2)),(rand()%5)+1,colours[(rand()%9)],'T');
		}
		if (mistakes[16])
			fprintf(mapfile,"%d,%d,%s,%c\n",(int)(nc/2),4,"GREEN",'T');
		if (mistakes[17])
			fprintf(mapfile,"%d,%d,%d,%s,%c\n",(int)(nc/2),(int)(nc/2),4,"RED",'N');
		if (mistakes[18]){
			fprintf(mapfile,"%d,%d,%d,%s,%c\n",(int)(nc/2),(int)(nc/2+1),0,"BLUE",'N');
			fprintf(mapfile,"%d,%d,%d,%s,%c\n",(int)(nc/2+1),(int)(nc/2),100,"ALL",'N');
			fprintf(mapfile,"%d,%d,%d,%s,%c\n",(int)(nc/2+1),(int)(nc/2+1),-4,"BLACK",'N');
		}
		if (mistakes[22])
			fprintf(mapfile,"%d,%d,%d,%s,%c\n",(int)(nc+10),(int)(nc/2+1),0,"BLUE",'N');
	}
	/* missions: */
	if (!mistakes[3])
		fprintf(mapfile,"Auftraege:\n");
	if (!mistakes[10]){
		for (int i=0;i<nm;i++){
			fprintf(mapfile,"%d,%d,%d\n",i%(nc/2),(i%(nc/2))+(nc/2)-(i/nc),rand()%45);
		}
		if (mistakes[19]){
			fprintf(mapfile,"%d,%d,%d\n",nc-2,nc-2,42);
			fprintf(mapfile,"%d,%d,%d\n",nc-2,nc-2,42);
		}
		if (mistakes[20])
			fprintf(mapfile,"%d,%d\n",rand()%nc,rand()%55);
		if (mistakes[21])
			fprintf(mapfile,"%d,%d,%d\n",nc+33,rand()%nc,rand()%55);	
	}
	fprintf(mapfile,"#DIESER ABschnitt iST NuR ZumM entNerveN DeR LESEEEEER Da, un\n#d SOLllTee eIgentlich Nciht vom PC NichT IgnoRieRT WERdEN\n");
	if (mistakes[23])
		fprintf(mapfile,"Dieser Kommentar allerdings ist Fehlerhaft!");
	
	
	return 0;
	
}
