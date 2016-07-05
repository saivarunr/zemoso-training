data=[]
for i in xrange(100):
    data.append(map(int,raw_input().strip().split(' ')))
i=1
l=len(data)
while i<l:
    data[i][0]+=data[i-1][0]
    data[i][i]+=data[i-1][i-1]
    j=1
    while j<i:
        data[i][j]+=max(data[i-1][j],data[i-1][j-1])
        j+=1
    i+=1
print max(data[l-1])
